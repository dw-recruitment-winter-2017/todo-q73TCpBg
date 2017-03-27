(ns organizer.views
  (:require [organizer.views.todo-heading :refer [todo-heading]]
            [re-frame.core :as re-frame]
            [re-com.core :as re-com]))

(defn title []
  (let [title (re-frame/subscribe [:app/title])]
    (fn []
      [re-com/title
       :level :level1
       :label @title])))

    (fn []


  [re-com/h-box


(defn main-panel []
  (fn []
    [re-com/v-box
     :align :center
     :height "100%"
     :margin "25px"
     :children [[title]
                [todo-heading]]]))
