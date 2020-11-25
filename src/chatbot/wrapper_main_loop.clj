(ns chatbot.wrapper_main_loop
  (:require [chatbot.core :refer [help-function possible-error-messages]]
            [chatbot.identify_keyword :refer [keyword-response-main]]
            [chatbot.find_park_data :refer [find-park-data]]
            [chatbot.greet :refer [greeting possible-greetings]]))

(defn wrapper-main-loop
  "Receives user input until a terminating keyword is met.
   The main loop calls help function if user input is help.
   Checks if the keyword is not identified and prints the random error message.
   Otherwise greets user or answers the questions about the park."
  []
  (println "Chatbot> Hi!")
  (println "Chatbot> I am your park guide. I will tell you about Bertramka park. 
            To end the conversation, enter 'finish'. Ask your questions")
  (loop [user-input (read-line)]
    (when-not (= "finish" user-input)
     (cond
       (= "help" user-input)
       (println (help-function))

       (and (= false (greeting possible-greetings user-input))
            (= false (keyword-response-main user-input)))
       (println (rand-nth possible-error-messages))

       (not (= false (greeting possible-greetings user-input)))
       (println (greeting possible-greetings user-input))

       (not (= false (keyword-response-main user-input)))
       (println (find-park-data (keyword-response-main user-input))))
      (recur (read-line)))))