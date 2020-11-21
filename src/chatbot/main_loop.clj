(defn main-loop
  "Receives user input until a terminating keyword is met"
  []
  (println "Chatbot> Hi!")
  (println (str "Chatbot> I am your park guide. I will tell you about "
                "Bertramka park. To end the conversation, enter 'finish'. "
                "Ask your questions"))
  (loop [user-input (read-line)]
    (when-not (= "finish" user-input)
      (recur (read-line)))))
