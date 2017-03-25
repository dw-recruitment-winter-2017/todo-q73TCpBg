(ns organizer.events
  (:require [re-frame.core :as re-frame]
            [organizer.db :as db]))

(re-frame/reg-event-db :initialize-db (fn  [_ _]
                                        db/seed))
