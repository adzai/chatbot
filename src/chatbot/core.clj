(ns chatbot.core
  (:require [chatbot.identify_keyword :refer [keyword-response-main]]
            [chatbot.parse :refer [parse-input]]
            [chatbot.bot_utils :as bot]
            [chatbot.park_utils :as park]
            [chatbot.user_utils :as user]
            [chatbot.cli_utils :as cli]
            [chatbot.decision_tree :as dec-tree]
            [web.backend :as web])
  (:gen-class))

(defn -main
  "When called without arguments a REPL chatbot is started.
  It consumes user input until a terminating keyword is met.
  The main loop calls the help function if user input is help.
  It also checks if the keyword is not identified and prints
  a random error message.
  Otherwise greets user, answers questions about the park or
  tries to identify a bird.
  When run with a --web flag, a web server will be started instead
  of the REPL."
  [& args]
  (when (cli/start-web-server? args)
    (web/run-backend! args))
  (cli/display-help? args)
  (bot/bot-print! "Hi!")
  (bot/bot-print! "I am your park guide.")
  (user/set-user-prompt!)
  (bot/bot-print! (str "You can change your username at any time "
                       "by typing 'username'."))
  (park/user-select-park!)
  (bot/bot-print! (str
                   "To end the conversation, enter the terminating keyword, "
                   "such as 'exit', 'quit', 'end', 'terminate' or 'bye'."))
  (bot/bot-print! "If you need help, type 'help'.")
  (bot/bot-print! "History of the park can be viewed by entering 'history'.")
  (bot/bot-print! "If you want to change the park type 'park'.")
  (bot/bot-print! (str
                   "By typing the keyword - 'bird',the bot will help you "
                   "to identify the birds of Prague parks."))
  (loop [user-input (parse-input (user/get-user-input))]
    (if (bot/finish? user-input)
      (bot/bot-print! (rand-nth bot/possible-goodbye-messages))
      (let [help? (= '("help") user-input)
            username-change? (= '("username") user-input)
            park-history? (= '("history") user-input)
            greeting? (bot/greeting bot/possible-greetings user-input)
            park-change? (= '("park") user-input)
            response (keyword-response-main user-input)
            bird-info? (= '("bird") user-input)]
        (cond
          help?
          (bot/help-function)

          username-change?
          (user/set-user-prompt!)

          park-history?
          (bot/bot-print! (park/park-history))

          greeting?
          (bot/bot-print! greeting?)

          park-change?
          (park/user-select-park!)

          bird-info?
          (dec-tree/questions-loop dec-tree/bird-decision-tree)

          response
          (bot/bot-print! (park/find-park-data response))

          :else (user/handle-unrecognized-sentence))

        (recur (parse-input (user/get-user-input)))))))
