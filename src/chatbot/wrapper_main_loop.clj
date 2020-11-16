(defn main-loop
  "Receives user input until a terminating keyword is met.The main loop calls help function if user input is help.
   Checks if the keyword is not identified and prints the random error message.
   Otherwise greets user or answers the questions about the park."
  []
  (println "Chatbot> Hi!")
  (println "Chatbot> I am your park guide. I will tell you about Bertramka park. To end the conversation, enter 'finish'. Ask your questions")
  (loop [user-input (read-line)]
    (when-not (= "finish" user-input)
      (if (= "help" user-input)
        (help-function))
      (if (and (= true (greeting possible-greetings user-input)) (= true (keyword-response-main user-input)))
          (println (rand-nth possible-error-messages)))
      (recur (read-line)))))
