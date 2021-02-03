(ns ring-app.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn handler [request-map]
  (response/response
    (str "<html><body>Hello, your IP is: "
         (:remote-addr request-map)
         "</body></html>")))

(defn wrap-nocache [handler]
  (fn [request-map]
    (-> request-map
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(defn -main []
  (jetty/run-jetty
    (-> #'handler
        wrap-nocache
        wrap-reload)
    {:port 3000
     :join? false}))