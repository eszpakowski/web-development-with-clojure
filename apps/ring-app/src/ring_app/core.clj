(ns ring-app.core
  (:require [reitit.ring :as reitit]
            [ring.adapter.jetty :as jetty]
            [ring.util.http-response :as response]
            [ring.middleware.reload :refer [wrap-reload]]
            [muuntaja.middleware :as muuntaja]))

(defn response-handler [request-map]
  (response/ok
    (str "<html><body>Hello, your IP is: "
         (:remote-addr request-map)
         "</body></html>")))

(defn json-handler [request-map]
  (response/ok
    {:result (get-in request-map [:body-params :id])}))

(def routes
  [["/" {:get  response-handler
         :post response-handler}]
   ["/resource/:id"
    {:get
     (fn [{{:keys [id]} :path-params}]
       (response/ok (str "<p>passed resource id is:" id "</p>")))}]])

(def handler
  (reitit/ring-handler
    (reitit/router routes)))

(defn wrap-nocache [handler]
  (fn [request-map]
    (-> request-map
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(defn wrap-formats [handler]
  (-> handler
      (muuntaja/wrap-format)))

(defn -main []
  (jetty/run-jetty
    (-> #'handler
        wrap-nocache
        wrap-formats
        wrap-reload)
    {:port  3000
     :join? false}))