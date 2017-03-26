(ns organizer.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [organizer.db :as db]
            [re-frame.core :as re-frame]))

(re-frame/reg-sub :app/title (fn [db _]
                               (db/get-app-title db)))

(re-frame/reg-sub :app/accepting-input (fn [db _]
                                         (db/accepting-input? db)))

