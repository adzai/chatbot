(ns chatbot.bot_utils
  (:require [chatbot.parse :refer [parse-input]]
            [chatbot.levenshtein :refer [similarity]]))

(def bot-prompt "Chatbot> ")

(def possible-error-messages
  (vector "Sorry, I didn't understand you, please try again!"
          (str "The Bot did not understand the question, "
               "please try in different words.")
          "I am sorry, I could not understand you"))

(def possible-greetings
  (vector "hey" "hi" "hello" "morning" "evening" "afternoon"))

(def possible-goodbye-messages
  (vector "See you next time!"
          "Goodbye!"
          "Bye!"))

(def responses
  (vector "Hello, thanks for visiting"
          "Good to see you again"
          "Hi there, how can I help?"))

  (defn bot-print!
    "Format's the message with a bot-prompt and prints it out"
    [msg]
    (println (str bot-prompt msg)))

  (defn help-function
    "Describes the functionalities of Chatbot."
    []
    (bot-print! (str "The chatbot is designed to answer users' "
                     "questions regarding Bertramka park."))
    (bot-print! (str "The bot provides information about following aspects "
                     "of Bertramka: wc, attractions, biking, skating, "
                     "sports field, playground, transportation and parking."))
    (bot-print! (str "Error messages are used to inform user that "
                     " asked questions are obscure to chatbot."))
    (bot-print! (str "The user can finish the conversation by "
                     "typing word - finish.")))

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
