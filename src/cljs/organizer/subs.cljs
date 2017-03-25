(ns organizer.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [organizer.db :as db]
            [re-frame.core :as re-frame]))

(re-frame/reg-sub :title (fn [db]
                           (::db/title db)))
