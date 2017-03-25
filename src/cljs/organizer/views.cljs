(ns organizer.views
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]))

(defn title []
  (let [title (re-frame/subscribe [:title])]
    (fn []
      [re-com/title
       :label @title
       :level :level1])))

(defn main-panel []
  (fn []
    [re-com/v-box
     :height "100%"
     :children [[title]]]))
