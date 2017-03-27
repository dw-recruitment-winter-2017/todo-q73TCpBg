(ns organizer.views.todo-item
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]))

(defn status-button [todo-id]
  (let [completed (re-frame/subscribe [:todo/completed todo-id])]
    (fn []
      (let [icon (if @completed "zmdi-check" "zmdi-circle-o")
            tip (if @completed "Mark as incomplete" "Mark as done")
            click-event (if @completed
                          [:update-todo-status todo-id false]
                          [:update-todo-status todo-id true])]
        [re-com/md-icon-button
         :md-icon-name icon
         :size :larger
         :tooltip tip
         :on-click (fn [] (re-frame/dispatch click-event))]))))

(defn delete-button [todo-id]
  [re-com/md-icon-button
   :md-icon-name "zmdi-delete"
   :size :larger
   :tooltip "Delete this entry"
   :on-click (fn [])])

(defn todo-description [todo-id]
  (let [description (re-frame/subscribe [:todo/description todo-id])]
    (fn []
      [re-com/title
       :level :level3
       :label @description])))

(defn todo-view [todo-id]
  [re-com/h-box
   :gap "5px"
   :children [[status-button todo-id]
              [todo-description todo-id]
              [re-com/gap :size "1"]
              [delete-button]]])
