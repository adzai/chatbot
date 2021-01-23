(ns chatbot.bot_utils
  (:require [chatbot.levenshtein :refer [similarity]]))

(def bot-prompt "Chatbot prompt in the REPL" "Chatbot> ")

(def possible-error-messages "Vector of various error messages"
  (vector "Sorry, I didn't understand you, please try again!"
          (str "The Bot did not understand the question, "
               "please try in different words.")
          "I am sorry, I could not understand you"))

(def possible-greetings "Vector of various greetings"
  (vector "hey" "hi" "hello" "morning" "evening" "afternoon"))

(def possible-goodbye-messages "Vector of various goodbye messages"
  (vector "See you next time!"
          "Goodbye!"
          "Bye!"))

(def responses "Vector of various responses"
  (vector "Hello, thanks for visiting"
          "Good to see you again"
          "Hi there, how can I help?"))

(def terminating-keywords (vector "quit" "exit" "end" "terminate" "bye"))

(defn bot-print!
  "Format's the message with a bot-prompt and prints it out"
  [msg]
  (println (str bot-prompt msg)))

(defn help-function
  "Describes the functionalities of Chatbot."
  []
  (bot-print! (str "The chatbot is designed to answer users' "
                   "questions regarding the park of their choice."))
  (bot-print! (str "The bot provides information about various aspects "
                   "such as wc, attractions, biking, skating, "
                   "sports field, playground, transportation, parking "
                   "and more."))
  (bot-print! (str "The user can get familiarized with the history of "
                    "specific park by typing the word - history."))
  (bot-print! (str "Error messages are used to inform user that "
                   " asked questions are obscure to the chatbot."))
  (bot-print! (str "The user can change their username by "
                   "typing the word - username."))
  (bot-print! (str "The user can change the park the chatbot is "
                   "answering questions about by typing the word - park."))
  (bot-print! (str "The chatbot can help the user to identify the birds"
                   " of Prague parks, based on given characteristics."
                   "For this, the user should type keyword - 'bird'."))
  (bot-print! (str "The user can finish the conversation by "
                   "typing the terminating keyword, such as 'exit',
                    'quit', 'end', 'terminate' or 'bye'.")))

(defn greeting
  "Using similarity function, identifies if
  the user input is in possible-greetings.
  If true, function prints random greeting message.
  Otherwise returns false."
  [greeting-vector input]
  (let [words input]
    (if (seq greeting-vector)
      (let [max-similarity
            (apply max
                   (for [word words]
                     (similarity word (first greeting-vector))))]
        (if (> max-similarity 0.7)
          (rand-nth responses)
          (greeting (rest greeting-vector) input)))
      false)))

(defn finish? [input]
  (if
   (and (= 1 (count input)) (some #(= (first input) %) terminating-keywords))
    true
    false))
