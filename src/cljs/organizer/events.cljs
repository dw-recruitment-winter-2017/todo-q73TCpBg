(ns organizer.events
  (:require [organizer.config :as config]
            [organizer.db :as db]
            [organizer.utils :as utils :refer [transit-response
                                               transit-request]]
            [day8.re-frame.http-fx]
            [re-frame.core :as re-frame]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; event handler registration                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^:private debug-interceptors
  (let [db-validator (re-frame/after #(when % (utils/validate ::db/state %)))]
    [re-frame/debug db-validator]))

(defn reg-event-db
  ([evt handler] (reg-event-db evt [] handler))

  ([evt interceptors handler]
   (re-frame/reg-event-db
    evt
    [(when config/debug? debug-interceptors), interceptors]
    handler)))

(defn reg-event-fx
  ([evt handler] (reg-event-fx evt [] handler))

  ([evt interceptors handler]
   (re-frame/reg-event-fx
    evt
    [(when config/debug? debug-interceptors), interceptors]
    handler)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; events                                                                   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;; seed the database
(reg-event-db :initialize-db (fn [_ _] db/seed))


;;;; application controls
(reg-event-db :accept-todo-input (fn [db _]
                                   (db/accept-todo-input db)))

(reg-event-db :block-todo-input (fn [db _]
                                  (db/block-todo-input db)))


;;;; interact with the server
(reg-event-fx
 :fetch-todos
 (fn [_ _]
   {:http-xhrio {:method          :get
                 :uri             "/todos/"
                 :response-format transit-response
                 :on-success      [:load-todo-list]}}))

(reg-event-fx
 :create-todo
 (fn [_world [_ val]]
   {:http-xhrio {:method          :post
                 :uri             "/todos/"
                 :params          {:todo {:description val
                                          :completed false}}
                 :format          transit-request
                 :response-format transit-response
                 :on-success      [:load-todo]}}))


;;;; todo data loading
(reg-event-db :load-todo-list (fn [db [_ todo-attr-list]]
                                (db/load-todo-list db todo-attr-list)))

(reg-event-fx :load-todo (fn [{:keys [db]} [_ todo-attrs]]
                           {:db (db/load-todo db todo-attrs)
                            :dispatch [:block-todo-input]}))
