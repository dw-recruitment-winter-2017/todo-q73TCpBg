(ns organizer.system
  (:require [organizer.component.postgres :refer [postgres]]
            [organizer.endpoint.example :refer [example-endpoint]]
            [organizer.endpoint.todo-endpoint :refer [todo-routes]]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [duct.component.endpoint :refer [endpoint-component]]
            [duct.component.handler :refer [handler-component]]
            [duct.component.ragtime :refer [ragtime]]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [duct.middleware.route-aliases :refer [wrap-route-aliases]]
            [meta-merge.core :refer [meta-merge]]
            [ring.component.jetty :refer [jetty-server]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.format :as format]
            [ring.middleware.webjars :refer [wrap-webjars]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; middleware                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- wrap-format [handler formats]
  (format/wrap-restful-format handler :formats formats))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; config                                                                   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def base-config
  {:app {:middleware [[wrap-not-found :not-found]
                      [wrap-webjars]
                      [wrap-format :formats]
                      [wrap-defaults :defaults]
                      [wrap-route-aliases :aliases]]

         :aliases    {"/" "/index.html"}

         :defaults   (meta-merge site-defaults
                                 {:static {:resources "organizer/public"}})

         :formats    [:json :transit-json]

         :not-found  (io/resource "organizer/errors/404.html")}

   :ragtime {:resource-path "organizer/migrations"}})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; system                                                                   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn new-system [config]
  (let [config (meta-merge base-config config)]
    (-> (component/system-map
         :app     (handler-component (:app config))
         :http    (jetty-server (:http config))
         :db      (postgres (:db config))
         :ragtime (ragtime (:ragtime config))
         :example (endpoint-component example-endpoint)
         :todo    (endpoint-component todo-routes))

        (component/system-using
         {:http    [:app]
          :app     [:example :todo]
          :ragtime [:db]
          :todo    [:db]}))))
