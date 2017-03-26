(ns organizer.events
  (:require [organizer.config :as config]
            [organizer.db :as db]
            [organizer.utils :as utils :refer [transit-reader]]
            [ajax.core :as ajax]
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
(reg-event-db :initialize-db (fn  [_ _] db/seed))

;;;; bulk todo data load
(reg-event-fx
 :fetch-todos
 (fn [_ _]
   {:http-xhrio {:method :get
                 :uri "/todos/"
                 :response-format (ajax/transit-response-format
                                   {:reader transit-reader})
                 :on-success [:load-todos]}}))

(reg-event-db :load-todos (fn [db [_ todo-attrs]]
                            (db/load-todos db todo-attrs)))

;;;; create new todos
(reg-event-db :accept-todo-input (fn [db _]
                                   (db/accept-todo-input db)))

(reg-event-db :block-todo-input (fn [db _]
                                  (db/block-todo-input db)))

(reg-event-fx :create-todo (fn [_ _]))
