(ns chatbot.core
  (:require [chatbot.get_data :refer [create-data]]
            [chatbot.identify_keyword :refer [keyword-response-main]]
            [chatbot.find_park_data :refer [find-park-data]]
            [chatbot.greet :refer [greeting possible-greetings]]))

(def possible-error-messages
  (vector ">Chatbot: Sorry, I didn't understand you, please try again!"
          ">Chatbot: The Bot did not understand the question,
           please try in different words."
          ">Chatbot: I am sorry, I could not understand you"))

(defn help-function
  "Describes the functionalities of Chatbot."
  []
  ">Chatbot: The chatbot is designed to answer users'
   questions regarding Bertramka park.
   The bot provides information about following aspects
   of Bertramka: wc, attractions, biking, skating,
   sports field, playground, transportation and parking.
   Error messages are used to inform user that
   asked questions are obscure to chatbot.
   The user can finish the conversation by typing word - finish.")

(defn wrapper-main-loop
  "Receives user input until a terminating keyword is met.
   The main loop calls help function if user input is help.
   Checks if the keyword is not identified and prints the random error message.
   Otherwise greets user or answers the questions about the park."
  []
  (println "Chatbot> Hi!")
  (println "Chatbot> I am your park guide.
            I will tell you about Bertramka park.
            To end the conversation, enter 'finish'.
            Ask your questions")
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


(defn main
  ""
  []
  (wrapper-main-loop)
  (create-data))
