(ns organizer.db
  (:require [organizer.data.todo :as todo :refer [todo]]
            [cljs.spec :as spec]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; data specs                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(spec/def ::current-page #{:about :todos})
(spec/def ::title string?)
(spec/def ::accepting-input boolean?)
(spec/def ::todos (spec/map-of ::todo/id ::todo/entity))

(spec/def ::state (spec/keys :req [::title
                                   ::current-page
                                   ::accepting-input
                                   ::todos]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; data accessors                                                           ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;; application state
(defn get-app-current-page [db]
  (::current-page db))

(defn get-app-title [db]
  (::title db))

(defn accept-todo-input [db]
  (assoc db ::accepting-input true))

(defn block-todo-input [db]
  (assoc db ::accepting-input false))

(defn accepting-input? [db]
  (::accepting-input db))

(defn get-current-page [db]
  (::current-page db))

(defn set-current-page [db page]
  (if (= page "/about")
    (assoc db ::current-page :about)
    (assoc db ::current-page :todos)))

;;;; todo data model
(defn load-todo-list [db attr-list]
  (->> attr-list
       (map #(todo %))
       (map (fn [t] [(::todo/id t) t]))
       (into {})
       (assoc db ::todos)))

(defn load-todo [db attrs]
  (let [todo-list (::todos db)
        todo (todo attrs)
        id (::todo/id todo)
        new-todo-list (assoc todo-list id todo)]
    (assoc db ::todos new-todo-list)))

(defn remove-todo [db todo-id]
  (let [todo-list (::todos db)
        new-todo-list (dissoc todo-list todo-id)]
    (assoc db ::todos new-todo-list)))

(defn get-todo-ids [db]
  (->> db
       (::todos)
       (keys)
       (into [])))

(defn get-todo [{:keys [::todos] :as db} todo-id]
  (get todos todo-id))

(defn get-todo-description [db todo-id]
  (-> db
      (get-todo todo-id)
      (::todo/description)))

(defn todo-completed? [db todo-id]
  (-> db
      (get-todo todo-id)
      (::todo/completed)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; database seed                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def seed
  {::current-page :todos
   ::title "Organize with LOVE!"
   ::accepting-input false
   ::todos {}})
