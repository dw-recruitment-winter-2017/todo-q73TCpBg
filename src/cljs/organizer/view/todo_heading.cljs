(ns organizer.views.todo-heading
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [reagent.core :refer [atom]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; label text                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn todo-label []
  [re-com/title
   :level :level2
   :underline? true
   :label "Todos:"])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; new button                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn new-todo-button []
  (let [input-enabled? (re-frame/subscribe [:app/accepting-input])]
    (fn []
      (let [disable-input! [:block-todo-input]
            enable-input! [:accept-todo-input]
            click-event (if @input-enabled? disable-input! enable-input!)]
        [re-com/button
         :label (if @input-enabled? "Cancel" "New Todo")
         :tooltip (if @input-enabled? "Never mind" "Add Some Love!")
         :tooltip-position :above-center
         :class (if @input-enabled? "btn-secondary" "btn-primary")
         :on-click (fn [] (re-frame/dispatch click-event))]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; new todo form                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn todo-description-input [description]
  [re-com/input-text
   :placeholder "Description"
   :on-change (fn [d] (reset! description d))
   :model description])

(defn todo-submission-button [description]
  [re-com/button
   :label "Save"
   :class "btn-success"
   :on-click (fn []
               (re-frame/dispatch [:create-todo @description])
               (reset! description ""))])

(defn new-todo-form []
  (let [enabled? (re-frame/subscribe [:app/accepting-input])
        description (atom "")]
    (fn []
      (if @enabled?
        [re-com/h-box
         :margin "10px 0"
         :gap "5px"
         :children [[todo-description-input description]
                    [todo-submission-button description]]]
        (do
          (reset! description "")
          [:div])))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; todo heading                                                             ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn new-todo-control []
  [re-com/v-box
   :align :end
   :children [[new-todo-button]
              [new-todo-form]]])

(defn todo-heading []
  [re-com/h-box
   :width "65%"
   :children [[todo-label] [re-com/gap :size "1"] [new-todo-control]]])
