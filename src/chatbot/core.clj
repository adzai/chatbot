(ns chatbot.core
  (:require [chatbot.get_data :refer [create-data]]))

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

(defn main
  ""
  []
  (create-data))
