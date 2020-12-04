(ns chatbot.core
  (:require [chatbot.identify_keyword :refer [keyword-response-main]]
            [chatbot.find_park_data :refer [find-park-data]]
            [chatbot.parse :refer [parse-input]]
            [chatbot.bot_utils :as bot]
            [chatbot.user_utils :as chat-user]))


(defn main-loop
  "Receives user input until a terminating keyword is met.
  The main loop calls help function if user input is help.
  Checks if the keyword is not identified and prints the random error message.
  Otherwise greets user or answers the questions about the park."
  []
  (bot/bot-print! "Hi!")
  (bot/bot-print! (str "I am your park guide. "
                       "I will tell you about Bertramka park. "
                       "To end the conversation, enter 'finish'. "
                       "Ask your questions."))
  (chat-user/set-user-prompt!)
  (bot/bot-print! "You can change your username anytime by typing 'username'")
  (bot/bot-print! "Feel free to ask any question about Bertramka!")
  (loop [user-input (parse-input (chat-user/get-user-input))]
     (if (and (= 1 (count user-input)) (some #(= "finish" %) user-input))
       (bot/bot-print! (rand-nth bot/possible-goodbye-messages))
       (do
         (cond
           (= '("help") user-input)
           (bot/help-function)

           (= '("username") user-input)
           (chat-user/set-user-prompt!)

           (and (= false (bot/greeting bot/possible-greetings user-input))
                (= false (keyword-response-main user-input)))
           (bot/bot-print! (rand-nth bot/possible-error-messages))

           (not (= false (bot/greeting bot/possible-greetings user-input)))
           (bot/bot-print! (bot/greeting bot/possible-greetings user-input))

           (not (= false (keyword-response-main user-input)))
           (bot/bot-print! (find-park-data (keyword-response-main user-input))))

         (recur (parse-input (chat-user/get-user-input)))))))
