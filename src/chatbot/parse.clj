(ns chatbot.parse
  (:require [clojure.string :as str]
            [cheshire.core :refer [parse-string]]))

(defn parse-input
  "Accepts a string and returns a vector of words which are lower cased
  and which only contain letters"
  [input]
  (let [words (str/split input #" ")
        letter-words
        (map
          #(str/join
             ""
             (filter (fn [x] (Character/isLetter x)) %)) words)
        lower-cased-words (map str/lower-case letter-words)]
    lower-cased-words))

(defn parse-json
  "Reads in a JSON file and returns a clojure map"
  [file-name]
  (let [file (slurp file-name)]
    (parse-string
      file
      (fn [k]
        (keyword
          (str/lower-case
            ; In case words contain spaces replace them with a hyphen
            (str/join "-" (str/split k #" "))))))))
