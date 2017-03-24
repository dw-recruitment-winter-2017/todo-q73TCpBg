(ns organizer.system
  (:require [organizer.component.postgres :refer [postgres]]
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
            [ring.middleware.webjars :refer [wrap-webjars]]
            [organizer.endpoint.example :refer [example-endpoint]]))

(def base-config
  {:app {:middleware [[wrap-not-found :not-found]
                      [wrap-webjars]
                      [wrap-defaults :defaults]
                      [wrap-route-aliases :aliases]]
         :not-found  (io/resource "organizer/errors/404.html")
         :defaults   (meta-merge site-defaults {:static {:resources "organizer/public"}})
         :aliases    {"/" "/index.html"}}
   :ragtime {:resource-path "organizer/migrations"}})

(defn new-system [config]
  (let [config (meta-merge base-config config)]
    (-> (component/system-map
         :app     (handler-component (:app config))
         :http    (jetty-server (:http config))
         :db      (postgres (:db config))
         :ragtime (ragtime (:ragtime config))
         :example (endpoint-component example-endpoint))
        (component/system-using
         {:http    [:app]
          :app     [:example]
          :ragtime [:db]
          :example [:db]}))))