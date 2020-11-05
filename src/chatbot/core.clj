(ns chatbot.core
  (:require [chatbot.get_data :as data]
            [clojure.string :as str]))

(defn parse-input [input]
  (let [words (str/split input #" ")
        num_of_words (count words)]
    (loop [i 0
           lower-cased-words []]
       (if (< i num_of_words)
         (let [lower-cased-word (str/lower-case (nth words i))]
            (recur (inc i)
                   (conj lower-cased-words lower-cased-word)))
         lower-cased-words))))

(defn main
  ""
  []
  (data/create-data))
