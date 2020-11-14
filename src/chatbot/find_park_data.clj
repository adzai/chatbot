(defn find-park-data 
  "Checks what value of the keyword identified in user input is in the data
  structure containing data about the park and prints out the corresponding
  bot's answer depending on the keyword type"
  [keyword]
  (when-not (nil?(get park-map keyword))
    (cond
      (some #(= keyword %) ["wc" "playground" "parking"])
        (if (= "yes" (get (parse-json "data/synonyms.json") keyword))
          (println (format ">Chatbot: You can find %s in Bertramka. " keyword))
          (println (format ">Chatbot: Unfortunately, there is no %s in Bertramka. " keyword)))

      (some #(= keyword %) ["biking" "skating" "skiing"])
      (if (= "yes" (get (parse-json "data/synonyms.json") keyword))
        (println (format ">Chatbot: %s is possible in Bertramka. " keyword))
        (println (format ">Chatbot: Unfortunately, %s is not possible in Bertramka. " keyword)))

      (= keyword "attractions")
      (println (format ">Chatbot: In Bertramka you can find multiple attractions, some are: %s" (get (parse-json "data/synonyms.json") keyword)))

      (= keyword "transportation")
      (println (format ">Chatbot: You can get to Bertramka these ways:, they are: %s" (get (parse-json "data/synonyms.json") keyword)))

      (= keyword "dogs")
      (if (= "yes" (get (parse-json "data/synonyms.json") keyword))
        (println ">Chatbot: You can enter Bertramka with your dogs")
        (println ">Chatbot:  Unfortunately, you can't enter Bertramka with your dogs")))))
