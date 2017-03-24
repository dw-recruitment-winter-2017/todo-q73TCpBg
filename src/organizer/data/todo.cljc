(ns organizer.data.todo
  (:require [#?(:clj clojure.spec, :cljs cljs.spec) :as spec]))

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

(spec/def ::create-attrs (spec/keys :req-un [::id ::completed ::description
                                             ::created-at ::modified-at]))
(spec/def ::update-attrs (spec/keys :req-un [::modified-at]
                                    :opt-un [::completed ::description]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; validation                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- validate [spec attrs]
  (if (spec/valid? spec attrs)
    {:ok attrs}
    {:error (spec/explain spec attrs)}))

(defn validate-create [attrs]
  (validate ::create-attrs attrs))

(defn validate-update [attrs]
  (validate ::update-attrs attrs))
