(ns organizer.db-test
  (:require [organizer.db :as db]
            [organizer.data.todo :as store]
            [cljs.spec :as spec]
            [cljs.test :refer-macros [deftest is testing]]))

(deftest seed-test
  (testing "database seed"
    (let [subject db/seed]
      (is (spec/valid? ::db/state subject)
          "is a valid app state"))))
