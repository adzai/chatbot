(ns chatbot.bot_utils)

(def bot-prompt "Chatbot> ")

(def possible-error-messages
  (vector (str bot-prompt "Sorry, I didn't understand you, please try again!")
          (str bot-prompt "The Bot did not understand the question, "
           "please try in different words.")
          (str bot-prompt "I am sorry, I could not understand you")))

(defn help-function
  "Describes the functionalities of Chatbot."
  []
  (str bot-prompt "The chatbot is designed to answer users' "
   "questions regarding Bertramka park.\n"
   "The bot provides information about following aspects "
   "of Bertramka: wc, attractions, biking, skating, "
   "sports field, playground, transportation and parking.\n"
   "Error messages are used to inform user that "
   " asked questions are obscure to chatbot.\n"
   "The user can finish the conversation by typing word - finish."))
