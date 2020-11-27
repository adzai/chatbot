(ns chatbot.greet
  (:require [chatbot.parse :refer [parse-input]]
            [chatbot.levenshtein :refer [similarity]]
            [chatbot.bot_utils :as bot]))

(def possible-greetings
  (vector "hey" "hi" "hello" "morning" "evening" "afternoon"))

(def responses
  (vector (str bot/bot-prompt "Hello, thanks for visiting")
          (str bot/bot-prompt "Good to see you again")
          (str bot/bot-prompt "Hi there, how can I help?")))

(defn greeting
  "Using similarity function, identifies if
   the user input is in possible-greetings.
   If true, function prints random greeting message.
   Otherwise returns false."
  [greeting-vector input]
   (let [words (parse-input input)]
     (if (seq greeting-vector)
       (let [max-similarity
             (apply max
                    (for [y words]
                      (similarity y (first greeting-vector))))]
         (if (> max-similarity 0.7)
           (rand-nth responses)
           (greeting (rest greeting-vector) input)))
       false)))
