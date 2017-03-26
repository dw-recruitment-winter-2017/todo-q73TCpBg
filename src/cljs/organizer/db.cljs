(ns organizer.db
  (:require [organizer.data.todo :as todo :refer [todo]]
            [cljs.spec :as spec]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; data specs                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(spec/def ::title string?)
(spec/def ::accepting-input boolean?)
(spec/def ::todos (spec/map-of ::todo/id ::todo/entity))

(spec/def ::state (spec/keys :req [::title
                                   ::accepting-input
                                   ::todos]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; data accessors                                                           ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;; application state
(defn get-app-title [db]
  (::title db))

(defn accept-todo-input [db]
  (assoc db ::accepting-input true))

(defn block-todo-input [db]
  (assoc db ::accepting-input false))

(defn accepting-input? [db]
  (::accepting-input db))

;;;; data model
(defn load-todos [db attr-list]
  (->> attr-list
       (map #(todo %))
       (map (fn [t] [(::todo/id t) t]))
       (into {})
       (assoc db ::todos)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; database seed                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def seed
  {::title "Organize with LOVE!"
   ::accepting-input false
   ::todos {}})