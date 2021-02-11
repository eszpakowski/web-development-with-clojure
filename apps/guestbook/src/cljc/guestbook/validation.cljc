(ns guestbook.validation
  (:require
    [struct.core :as struct]))

(def message-schema
  [[:name
    struct/required
    struct/string]
   [:message
    struct/required
    struct/string
    {:message  "Message must have at least 10 characters!"
     :validate (fn [msg] (>= (count msg) 10))}]])

(defn validate-message [params]
  (first (struct/validate params message-schema)))