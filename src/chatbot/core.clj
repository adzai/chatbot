(ns chatbot.core
  (:require [chatbot.get_data :refer [create-data]]
            [chatbot.identify_keyword :refer [keyword-response-main]]
            [chatbot.find_park_data :refer [find-park-data]]
            [chatbot.greet :refer [greeting possible-greetings]]
            [chatbot.bot_utils :as bot_utils]))


; User prompt which can be later changed to user's actual name
; after asking for it in the conversation
(def user-prefix (ref "User> "))

(defn get-user-input
  [user-prefix]
  (print user-prefix)
  (flush)
  (read-line))

(defn wrapper-main-loop
  "Receives user input until a terminating keyword is met.
   The main loop calls help function if user input is help.
   Checks if the keyword is not identified and prints the random error message.
   Otherwise greets user or answers the questions about the park."
  []
  (println (str bot_utils/prefix "Hi!"))
  (println (str bot_utils/prefix "I am your park guide. "
            "I will tell you about Bertramka park. "
            "To end the conversation, enter 'finish'. "
            "Ask your questions."))
  (loop [user-input (get-user-input @user-prefix)]
    (when-not (= "finish" user-input)
     (cond
       (= "help" user-input)
       (println (bot_utils/help-function))

       (and (= false (greeting possible-greetings user-input))
            (= false (keyword-response-main user-input)))
       (println (rand-nth bot_utils/possible-error-messages))

       (not (= false (greeting possible-greetings user-input)))
       (println (greeting possible-greetings user-input))

       (not (= false (keyword-response-main user-input)))
       (println (find-park-data (keyword-response-main user-input))))
      (recur (get-user-input @user-prefix)))))


(defn main
  ""
  []
  (wrapper-main-loop)
  (create-data))
