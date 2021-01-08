(ns chatbot.identify_keyword
  (:require [chatbot.parse :refer [parse-json]]
            [chatbot.levenshtein :refer [similarity]]))

(def synonyms-map "Hash-map of the data/synonyms.json file"
  (parse-json "data/synonyms.json"))

(defn keyword-response-vector
  "Takes the vector of synonyms, identifies keyword
  and returns the corresponding response.
  If the keyword is not identified, function returns false"
  [synonyms-vector input]
  (let [words input
        synonyms synonyms-vector]
    (loop [synonyms-vec synonyms-vector]
      (let [synonym (first synonyms-vec)]
        (if (not (nil? synonym))
          (let [max-similarity
                (apply max
                       (for [word words]
                         (similarity word (first synonyms-vec))))]
            (if (>= max-similarity 0.75)
              (first synonyms)
              (recur (rest synonyms-vec))))
          false)))))

(defn keyword-response-list
  "Takes the list of vectors containing synonyms
  and calls keyword-respons-vector function on the first vector.
  If the keyword is not found in any of the vectors, then returns false"
  [synonyms-lst input]
  (if (seq synonyms-lst)
    (if (= false (keyword-response-vector (first synonyms-lst) input))
      (keyword-response-list (rest synonyms-lst) input)
      (keyword-response-vector (first synonyms-lst) input))
    false))

(defn keyword-response-main
  "Parses the user-input, retrieves values from the synonyms-map
  and calls keyword-response-list on the synonyms list"
  [input]
  (let [synonyms (vals synonyms-map)]
    (keyword-response-list synonyms input)))
