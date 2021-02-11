(ns guestbook.core
  (:require
    [clojure.string :as string]
    [reagent.core :as r]
    [reagent.dom :as dom]
    [ajax.core :refer [GET POST]]
    [guestbook.validation :refer [validate-message]]))

(defn send-message! [fields errors]
  (if-let [validation-errors (validate-message @fields)]
    (reset! errors validation-errors)
    (POST "/message"
          {:format  :json
           :headers {"Accept"       "application/transit+json"
                     "x-csrf-token" (.-value (.getElementById js/document "token"))}
           :params  @fields
           :handler #(do
                       (.log js/console (str "response:" %))
                       (reset! errors nil))
           :error-handler
                    #(do
                       (.log js/console (str "error:" %))
                       (reset! errors (get-in % [:response :errors])))})))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (string/join error)]))

(defn message-form []
  (let [fields (r/atom {})
        errors (r/atom {})]
    (fn []
      [:div
       [errors-component errors :server-error]
       [:div.field
        [:label.label {:for :name} "Name"]
        [:input.input
         {:type      :text
          :name      :name
          :on-change #(swap! fields assoc :name (-> % .-target .-value))
          :value     (:name @fields)}]]
       [:div.field
        [:label.label {:for :message} "Message"]
        [:textarea.textarea
         {:name      :message
          :value     (:message @fields)
          :on-change #(swap! fields assoc :message (-> % .-target .-value))}]]
       [:input.button.is-primary
        {:type     :submit
         :on-click #(send-message! fields errors)
         :value    "comment"}]
       [:hr]
       [:p "Name: " (:name @fields)]
       [:p "Message: " (:message @fields)]])))

(defn home []
  [:div.content>div.columns.is-centered>div.column.is-two-thirds
   [:div.columns>div.column
    [message-form]]])

(dom/render
  [home]
  (.getElementById js/document "content"))