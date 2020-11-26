(ns chatbot.bot_utils)

(def prefix "Chatbot> ")

(def possible-error-messages
  (vector (str prefix "Sorry, I didn't understand you, please try again!")
          (str prefix "The Bot did not understand the question, "
           "please try in different words.")
          (str prefix "I am sorry, I could not understand you")))

(defn help-function
  "Describes the functionalities of Chatbot."
  []
  (str prefix "The chatbot is designed to answer users' "
   "questions regarding Bertramka park.\n"
   "The bot provides information about following aspects "
   "of Bertramka: wc, attractions, biking, skating, "
   "sports field, playground, transportation and parking.\n"
   "Error messages are used to inform user that "
   " asked questions are obscure to chatbot.\n"
   "The user can finish the conversation by typing word - finish."))
