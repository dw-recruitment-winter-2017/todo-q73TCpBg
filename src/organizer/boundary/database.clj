(ns organizer.boundary.database
  (:require [organizer.data.todo :as todo]
            [clojure.core.match :refer [match]]))

(defprotocol TodoDatabase
  (list-todos [db])
  (save-todo! [db attrs])
  (set-todo! [db id attrs])
  (delete-todo! [db id]))

(defn create-todo! [db attrs]
  (let [validated (todo/validate-create attrs)]
    (match validated
      {:ok attrs} (try {:ok (save-todo! db attrs)}
                       (catch Exception e
                         {:error (.getMessage e)}))
      :else validated)))

(defn update-todo! [db id attrs]
  (let [validated (todo/validate-update attrs)]
    (match validated
      {:ok attrs} (try {:ok (set-todo! db id attrs)}
                       (catch Exception e
                         {:error (.getMessage e)}))
      :else validated)))
