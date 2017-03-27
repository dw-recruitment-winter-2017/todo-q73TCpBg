(ns organizer.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [organizer.db :as db]
            [re-frame.core :as re-frame]))

(re-frame/reg-sub :app/title (fn [db _]
                               (db/get-app-title db)))

(re-frame/reg-sub :app/accepting-input (fn [db _]
                                         (db/accepting-input? db)))

(re-frame/reg-sub :todo/list (fn [db _]
                               (db/get-todo-ids db)))

(re-frame/reg-sub :todo/description (fn [db [_ todo-id]]
                                      (db/get-todo-description db todo-id)))

(re-frame/reg-sub :todo/completed (fn [db [_ todo-id]]
                                    (db/todo-completed? db todo-id)))
