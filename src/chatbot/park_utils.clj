(ns chatbot.park_utils
  (:require [chatbot.bot_utils :as bot]
            [chatbot.user_utils :as chat-user]
            [chatbot.parse :refer [keyword->park parse-json]]))

(def park-name (ref ""))

(def keywords (keys (parse-json "data/data-en.json")))

(defn user-select-park
  "Prompt the user to select a park"
  []
  (bot/bot-print! (str "Select a park to get info for "
                       " (type corresponding number:"))
  (loop [i 1
         keywords keywords]
    (when-not (empty? keywords)
      (println (str i ": " (keyword->park (first keywords))))
      (recur (inc i) (rest keywords))))
  (loop []
    (let [raw-input (chat-user/get-user-input)
          input (if (= "" raw-input) false (read-string raw-input))]
      (if (and (number? input)
               (<= input (count keywords))
               (> input 0))
        (dosync (ref-set park-name
                         (keyword->park (nth keywords (dec input)))))
        (do
          (bot/bot-print! "Enter a valid number between 1-12")
          (recur)))))
  (bot/bot-print! (str "Ok, now I will answer your questions "
                       "about the " @park-name
                       " park!")))
