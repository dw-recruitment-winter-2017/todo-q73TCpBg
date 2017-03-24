(ns organizer.boundary.database
  (:require [clojure.core.match :refer [match]]))

(defprotocol TodoDatabase
  (list-todos [db])
  (create-todo! [db attrs])
  (update-todo! [db id attrs])
  (delete-todo! [db id]))
