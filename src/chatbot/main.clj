(defn main
  "Receives user input"
  []
  (println "Chatbot> Hi!")
  (println "Chatbot> I am your park guide. I will tell you about Bertramka park. To end the conversation, enter 'finish'. Ask your questions")
  (loop [user-input (do (read-line))]
    (when-not (= "finish" user-input)
      (recur (do (read-line))))))
