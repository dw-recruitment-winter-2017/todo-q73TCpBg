(ns organizer.test-utils
  (:require [organizer.data.todo :as todo]
            [organizer.db :as db]
            [cljs.spec :as spec]
            [clojure.set :refer [rename-keys]]
            [clojure.test.check.generators :as gen]))

(defn unqualify-keys [m]
  (let [keys (keys m)
        keymap (into {} (map (fn [k] [k (keyword (name k))]) keys))]
    (rename-keys m keymap)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; test data generators                                                     ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn generate [spec]
  (gen/generate (spec/gen spec)))

(defn test-todo []
  (generate ::todo/entity))

(defn test-todo-attrs []
  (-> (test-todo)
      (unqualify-keys)))

(defn test-db []
  (generate ::db/state))
