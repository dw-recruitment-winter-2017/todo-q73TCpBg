(ns organizer.utils)

(defn todo-url [{id :id :as todo}]
  (str "/todo/" id))

#?(:clj
   (defn current-time []
     (-> (System/currentTimeMillis)
         (java.sql.Timestamp.))))

#?(:clj
   (defn uuid []
     (java.util.UUID/randomUUID)))
