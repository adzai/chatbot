(def synonyms-map (parse-json "data/synonyms.json"))

(defn keyword-response-vector  
  "Takes the vector of synonyms, identifies keyword 
   and prints the corresponding response. 
   If the keyword is not identified, function returns false"
  [synonyms-vector input]
  (let [words (parse-input input)
        synonyms synonyms-vector]
  (loop [synonyms-vec synonyms-vector]
    (let [synonym (first synonyms-vec)]
      (if (not (nil? synonym)) 
        (let [max-similarity 
              (apply max 
                     (for [y words] (similarity y (first synonyms-vec))))]
          (if (> max-similarity 0.75)
            (first synonyms)
            (recur (rest synonyms-vec))))
        false)))))

(defn keyword-response-list
  "Takes the list of vectors containing synonyms 
   and calls keyword-response function on the first vector. 
   If the keyword is not found in any of the vectors, then returns false"
  [synonyms-lst input]
  (if (not (empty? synonyms-lst))
    (if (= false (keyword-response-vector (first synonyms-lst) input))
      (keyword-response-list (rest synonyms-lst) input))
    false)) 

(defn keyword-response-main 
  "Parses the user-input, retrieves values from the synonyms-map 
   and calls keyword-response-list on the synonyms list"
  [input]
  (let [words (parse-input input)
        synonyms (vals synonyms-map)]
    (keyword-response-list synonyms input)))
