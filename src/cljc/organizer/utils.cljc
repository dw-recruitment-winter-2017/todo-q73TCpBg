(ns organizer.utils
  (:require [cognitect.transit :as transit]
            [#?(:clj clojure.spec, :cljs cljs.spec) :as spec]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; urls                                                                     ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn todo-url [{id :id :as todo}]
  (str "/todos/" id))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; data (de)serialization                                                   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

#?(:clj
   (defn current-time []
     (-> (System/currentTimeMillis)
         (java.sql.Timestamp.))))

#?(:clj
   (defn uuid
     ([] (java.util.UUID/randomUUID))
     ([string] (java.util.UUID/fromString string))))

#?(:cljs
   (def transit-reader
    (transit/reader :json {:handlers {"u" cljs.core/uuid}})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; time parsing/formatting                                                  ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

#?(:cljs
   (defn string->date [string]
     (js/Date. string))

   :clj
   (let [format         "yyyy-MM-dd'T'HH:mm:ssZ"
         date-formatter (java.text.SimpleDateFormat. format)]
     (defn string->date [string]
       (.format date-formatter string))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; data validation                                                          ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn validate [s data]
  (if (spec/valid? s data)
    data
    (throw (ex-info "Validation failed" (spec/explain-data s data)))))
