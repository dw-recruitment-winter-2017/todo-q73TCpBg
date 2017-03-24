(ns organizer.component.postgres
  (:require [organizer.boundary.database :as database]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [clojure.string :refer [join]]
            [clojure.set :refer [rename-keys]]
            [com.stuartsierra.component :as component]
            [hikari-cp.core :refer [make-datasource close-datasource]]
            [honeysql.core :as sql]
            [honeysql.format :as sql.fmt]
            [honeysql.helpers :as sql-helper :refer [defhelper delete-from
                                                     from insert-into select
                                                     sset update values where]]
            [jdbc.core :as jdbc]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; db connection spec                                                       ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- connection-spec [postgres]
  {:datasource (make-datasource postgres)})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; querying                                                                 ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmethod sql.fmt/format-clause :returning [[_ fields] _]
  (str "RETURNING " (join ", " (map sql.fmt/to-sql fields))))

(defhelper returning [m args]
  (assoc m :returning args))

(defn- sanitize-keys [m]
  (let [keymap (into {} (map (fn [k] {k (->kebab-case-keyword k)})
                             (keys m)))]
    (rename-keys m keymap)))

(defn- query [db-spec statement]
  (with-open [conn (jdbc/connection db-spec)]
    (->> statement
         (sql/format)
         (jdbc/fetch conn)
         (map #(sanitize-keys %)))))

(defn- query-one [db-spec statement]
  (first (query db-spec statement)))

(defn- execute [db-spec statement]
  (with-open [conn (jdbc/connection db-spec)]
    (->> statement
         (sql/format)
         (jdbc/execute conn))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; component                                                                ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Postgres []
  component/Lifecycle
  (start [postgres]
    (if (:spec postgres)
      postgres
      (assoc postgres :spec (connection-spec postgres))))

  (stop [postgres]
    (if-let [datasource (-> postgres :spec :datasource)]
      (do (close-datasource datasource)
          (dissoc postgres :spec))
      postgres))

  database/TodoDatabase
  (list-todos [{db-spec :spec}]
    (query db-spec (-> (select :*)     ;; TODO: Paginate this!
                       (from :todos))))

  (save-todo! [{db-spec :spec} attrs]
    (query-one db-spec (-> (insert-into :todos)
                           (values [attrs])
                           (returning :*))))

  (set-todo! [{db-spec :spec} id attrs]
    (query-one db-spec (-> (update :todos)
                           (sset attrs)
                           (where [:= :id id])
                           (returning :*))))

  (delete-todo! [{db-spec :spec} id]
    (execute db-spec (-> (delete-from :todos)
                         (where [:= :id id])))))

(defn postgres [{:keys [db-name host] :as config}]
  (-> config
      (assoc :adapter "postgresql")
      (map->Postgres)))
