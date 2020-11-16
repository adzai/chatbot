(ns chatbot.greet
  (:require [chatbot.core :refer [parse-input]]
            [chatbot.levenshtein :refer [similarity]]))
  
  
(def possible-greetings (vector "hey" "hi" "hello" "morning" "evening" "afternoon"))

(def responses (vector ">Chatbot: Hello, thanks for visiting" ">Chatbot: Good to see you again" ">Chatbot: Hi there, how can I help?"))

(defn greeting
  "Using similarity function, identifies if the user-input is in possible-greetings, if true function prints random greeting message, otherwise retruns true."
  [greeting-vector input]
   (let [words (parse-input input)]
     (if (not (empty? greeting-vector))
       (let [max-similarity (apply max (for [y words] (similarity y (first greeting-vector))))]
         (if (> max-similarity 0.7)
           (rand-nth responses)
           (greeting (rest greeting-vector) input)))
       false)))
