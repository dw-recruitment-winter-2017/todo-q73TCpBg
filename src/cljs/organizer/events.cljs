(ns organizer.events
  (:require [organizer.config :as config]
            [organizer.db :as db]
            [organizer.utils :as utils]
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

(reg-event-db :initialize-db (fn  [_ _] db/seed))
