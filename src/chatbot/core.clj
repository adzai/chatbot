(ns chatbot.core
  (:require [chatbot.get_data :refer [create-data]]
            [chatbot.identify_keyword :refer [keyword-response-main]]
            [chatbot.find_park_data :refer [find-park-data]]
            [chatbot.greet :refer [greeting possible-greetings]]
            [chatbot.bot_utils :as bot]
            [chatbot.user_utils :as user]
            [clojure.string :as str]))



(defn wrapper-main-loop
  "Receives user input until a terminating keyword is met.
   The main loop calls help function if user input is help.
   Checks if the keyword is not identified and prints the random error message.
   Otherwise greets user or answers the questions about the park."
  []
  (println (str bot/bot-prompt "Hi!"))
  (println (str bot/bot-prompt "I am your park guide. "
            "I will tell you about Bertramka park. "
            "To end the conversation, enter 'finish'. "
            "Ask your questions."))
  (user/set-user-prompt!)
  (println (str bot/bot-prompt "You can change your username anytime by typing 'username'"))
  (println (str bot/bot-prompt "Now feel free to ask any question about Bertramka!"))
  (loop [user-input (str/lower-case (user/get-user-input))]
    (when-not (= "finish" user-input)
     (cond
       (= "help" user-input)
       (println (bot/help-function))

       (= "username" user-input)
       (user/set-user-prompt!)

       (and (= false (greeting possible-greetings user-input))
            (= false (keyword-response-main user-input)))
       (println (rand-nth bot/possible-error-messages))

       (not (= false (greeting possible-greetings user-input)))
       (println (greeting possible-greetings user-input))

       (not (= false (keyword-response-main user-input)))
       (println (find-park-data (keyword-response-main user-input))))
      (recur (user/get-user-input)))))


(defn main
  ""
  []
  (wrapper-main-loop)
  (create-data))
