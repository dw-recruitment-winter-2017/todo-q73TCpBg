(ns organizer.data.todo
  (:require [organizer.utils :refer [string->date uuid]]
            [#?(:clj clojure.spec, :cljs cljs.spec) :as spec]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; data specs                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- short-description? [desc]
  (<= (count desc) 256))

(spec/def ::id uuid?)
(spec/def ::completed boolean?)
(spec/def ::description (spec/and string? short-description?))
(spec/def ::created-at inst?)
(spec/def ::modified-at inst?)

(spec/def ::create-attrs (spec/keys :req-un [::id
                                             ::completed
                                             ::description
                                             ::created-at
                                             ::modified-at]))

(spec/def ::update-attrs (spec/keys :req-un [::modified-at]

                                    :opt-un [::completed
                                             ::description]))

(spec/def ::entity (spec/keys :req [::id
                                    ::completed
                                    ::description
                                    ::created-at
                                    ::modified-at]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; validation                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- validate [spec attrs]
  (if (spec/valid? spec attrs)
    {:ok attrs}
    {:error (spec/explain-str spec attrs)}))

(defn validate-create [attrs]
  (validate ::create-attrs attrs))

(defn validate-update [attrs]
  (validate ::update-attrs attrs))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; constructor                                                              ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn todo [{:keys [id completed description created-at modified-at]
             :as attrs}]
  (let [data {::id          id
              ::completed   completed
              ::description description
              ::created-at  created-at
              ::modified-at modified-at}]
    data))
