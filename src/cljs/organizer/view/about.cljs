(ns organizer.views.about
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]))

(defn title []
  [re-com/title
   :level :level1
   :label "About This App"])

(defn description []
  [re-com/v-box
   :children [[re-com/p
               (str "Here's an app that can help you get organized. List the "
                    "stuff you have to do, and mark when that stuff gets done. "
                    "You can even get rid of the stuff you don't want to keep "
                    "track of anymore.")]
               [re-com/p
                (str "What's the deal with all of the love stuff? I thought "
                     "you'd ask that. Love is better than hate. It's even "
                     "better than not love. Why not love?")]]])

(defn about-panel []
  [re-com/v-box
   :align :center
   :children [[title]
              [description]]])
