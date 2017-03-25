(ns organizer.runner
  (:require [organizer.db-test]
            [doo.runner :refer-macros [doo-tests]]))

(doo-tests 'organizer.db-test)
