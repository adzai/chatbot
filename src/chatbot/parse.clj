(ns chatbot.parse
  (:require [clojure.string :as str]
            [cheshire.core :refer [parse-string]]))

(defn park->keyword
  "Takes a park name e.g. 'Riegerovy Sady' and transforms it into
  a keyword :riegerovy-sady"
  [park-name]
  (-> park-name
      (str/replace #" " "-")
      (str/lower-case)
      (keyword)))

(defn parse-input [input]
  (let [words (str/split input #" ")
        letter-words
        (map
          #(str/join
             ""
             (filter (fn [x] (Character/isLetter x)) %)) words)
        lower-cased-words (map str/lower-case letter-words)]
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
