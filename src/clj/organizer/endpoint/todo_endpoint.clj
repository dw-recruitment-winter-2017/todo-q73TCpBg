(ns organizer.endpoint.todo-endpoint
  (:require [organizer.boundary.database :as db]
            [organizer.data.todo :as todo]
            [organizer.utils :refer [current-time todo-url uuid]]
            [clojure.core.match :refer [match]]
            [compojure.core :refer :all]
            [ring.util.response :as response :refer [response]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; responses                                                                ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- created [todo]
  (response/created (todo-url todo)
                    todo))

(defn- deleted [id]
  (let [deleted-message (format "todo item '%s' deleted" id)]
    (response {:message deleted-message})))

(defn- error [e status]
  (-> (response {:error e})
      (assoc :status status)))

(defn- ok [result]
  (response result))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; attribute sanitization                                                   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^:private user-attrs [:completed :description])

(defn- create-attrs [params id now]
  (-> params
      (select-keys user-attrs)
      (assoc :id id)
      (assoc :created-at now)
      (assoc :modified-at now)))

(defn- update-attrs [params now]
  (-> params
      (select-keys user-attrs)
      (assoc :modified-at now)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; actions                                                                  ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn list [db]
  (ok (db/list-todos db)))

(defn create [db params]
  (let [id  (uuid)
        now (current-time)
        attrs (create-attrs params id now)]
    (-> (db/create-todo! db attrs)
        (match {:ok todo}       (created todo)
               {:error message} (error message 409)))))

(defn update [db id params]
  (let [now (current-time)
        attrs (update-attrs params now)]
    (-> (db/update-todo! db id attrs)
        (match {:ok todo}       (ok todo)
               {:error message} (error message 400)))))

(defn delete [db id]
  (db/delete-todo! db id)
  (deleted id))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; routes                                                                   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn todo-routes [{db :db :as endpoint}]
  (context "/todos" []
    (GET "/" []
      (list db))

    (POST "/" [todo]
      (create db todo))

    (PUT "/:id" [id todo]
      (update db id todo))

    (DELETE "/:id" [id]
      (delete db id))))
