(ns ring-app.core
  (:require [reitit.ring :as reitit]
            [ring.adapter.jetty :as jetty]
            [ring.util.http-response :as response]
            [ring.middleware.reload :refer [wrap-reload]]
            [muuntaja.middleware :as muuntaja]))

(defn wrap-nocache [handler]
  (fn [request-map]
    (-> request-map
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(defn wrap-formats [handler]
  (-> handler
      (muuntaja/wrap-format)))

(defn response-handler [request-map]
  (response/ok
    (str "<html><body>Hello, your IP is: "
         (:remote-addr request-map)
         "</body></html>")))

(defn json-handler [request-map]
  (response/ok
    {:result (get-in request-map [:body-params :id])}))

(def routes
  [["/"
    {:get  response-handler
     :post response-handler}]
   ["/api"
    {:middleware [wrap-formats]}]
   ["/resource/:id"
    {:get
     (fn [{{:keys [id]} :path-params}]
       (response/ok
         (str "<p>passed resource id is:" id "</p>")))}]
   ["/extract-values"
    {:post
     (fn [{params :body-params}]
       (response/ok
         {:values (map (apply juxt [:a :b]) params)}))}]])

(def handler
  (reitit/routes
    (reitit/ring-handler
      (reitit/router routes))
    (reitit/create-resource-handler
      {:path "/"})
    (reitit/create-default-handler
      {:not-found
       (constantly (response/not-found "404 - Page not found"))
       :method-not-allowed
       (constantly (response/method-not-allowed "405 - Not allowed"))
       :not-acceptable
       (constantly (response/not-acceptable "406 - Not acceptable"))})))

(defn -main []
  (jetty/run-jetty
    (-> #'handler wrap-nocache wrap-reload)
    {:port  3000
     :join? false}))