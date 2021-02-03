(ns ring-app.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.http-response :as response]
            [ring.middleware.reload :refer [wrap-reload]]
            [muuntaja.middleware :as muuntaja]))

(defn html-handler [request-map]
  (response/ok
    (str "<html><body>Hello, your IP is: "
         (:remote-addr request-map)
         "</body></html>")))

(defn json-handler [request-map]
  (response/ok
    {:result (get-in request-map [:body-params :id])}))

(def handler json-handler)

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