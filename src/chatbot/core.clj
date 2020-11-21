(ns chatbot.core
  (:require [chatbot.get_data :refer [create-data]]
            [clojure.string :as str]
            [cheshire.core :refer [parse-string]]))

(defn parse-input [input]
  (let [words (str/split input #" ")
        lower-cased-words (map str/lower-case words)]
    lower-cased-words))

(defn parse-json [file-name]
  (let [file (slurp file-name)]
    (parse-string
      file
      (fn [k]
        (keyword
          (str/lower-case
            (clojure.string/join "-"
                                 (clojure.string/split k #" "))))))))

(def possible-error-messages
  (vector ">Chatbot: Sorry, I didn't understand you, please try again!"
          ">Chatbot: The Bot did not understand the question,
           please try in different words."
          ">Chatbot: I am sorry, I could not understand you"))

(defn main
  ""
  []
  (create-data))
