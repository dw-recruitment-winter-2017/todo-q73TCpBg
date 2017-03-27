(ns organizer.db-test
  (:require [organizer.db :as db]
            [organizer.data.todo :as todo]
            [organizer.test-utils :refer [test-db test-todo unqualify-keys]]
            [cljs.spec :as spec]
            [cljs.test :refer-macros [deftest is testing]]))

(deftest seed-test
  (testing "database seed"
    (let [subject db/seed]
      (is (spec/valid? ::db/state subject)
          "is a valid app state"))))

(deftest load-todo-test
  (testing "load-todo"
    (let [db (test-db)
          todo (test-todo)
          todo-id (::todo/id todo)
          unqualified-todo (unqualify-keys todo)
          subject (db/load-todo db unqualified-todo)]
      (is (= (db/get-todo subject todo-id) todo)
          "loads the todo into the database"))))

(deftest remove-todo-test
  (testing "remove-todo"
    (let [db (test-db)
          todo-id (first (db/get-todo-ids db))
          subject (db/remove-todo db todo-id)]
      (is (nil? (db/get-todo subject todo-id))
          "removes the todo item"))))
