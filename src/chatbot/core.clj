(ns chatbot.core
  (:require [chatbot.get_data :as data]
            [clojure.string :as str]))

(defn parse-input [input]
  (let [words (str/split input #" ")
        lower-cased-words (map str/lower-case words)]
    lower-cased-words))

(defn main
  ""
  []
  (data/create-data))
