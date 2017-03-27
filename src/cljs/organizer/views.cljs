(ns organizer.views
  (:require [organizer.views.todo-heading :refer [todo-heading]]
            [organizer.views.todo-item :refer [todo-view]]
            [re-frame.core :as re-frame]
            [re-com.core :as re-com]))

(defn title []
  (let [title (re-frame/subscribe [:app/title])]
    (fn []
      [re-com/title
       :level :level1
       :label @title])))

(defn todo-list []
  (let [todo-ids (re-frame/subscribe [:todo/list])]
    (fn []
      [re-com/v-box
       :width "65%"
       :gap "15px"
       :children (for [todo-id @todo-ids]
                   ^{:key todo-id} [todo-view todo-id])])))

(defn main-panel []
  (fn []
    [re-com/v-box
     :align :center
     :height "100%"
     :margin "25px"
     :children [[title]
                [todo-heading]
                [todo-list]]]))
