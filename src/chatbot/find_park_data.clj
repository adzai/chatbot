(defn find-park-data
  "Checks what value of the keyword identified in user input is in the data
  structure containing data about the park and prints out the corresponding
  bot's answer depending on the keyword type"
  [found-keyword]
  (let [park-data (get (parse-json "data/Bertramka.json") found-keyword)]
    (if-not (nil? park-data)
      (cond
        (some #(= found-keyword %) ["wc" "playground" "parking"])
          (if (= true park-data)
            (println (format ">Chatbot: You can find %s in Bertramka. " found-keyword))
            (println (format ">Chatbot: Unfortunately, there is no %s in Bertramka. " found-keyword)))

        (some #(= found-keyword %) ["biking" "skating" "skiing"])
          (if (= true park-data)
           (println (format ">Chatbot: %s is possible in Bertramka. " found-keyword))
           (println (format ">Chatbot: Unfortunately, %s is not possible in Bertramka. " found-keyword)))

        (= found-keyword "attractions")
          (println (format ">Chatbot: In Bertramka you can find multiple attractions, some are: %s" park-data))

        (= found-keyword "transportation")
          (println (format ">Chatbot: You can get to Bertramka these ways:, they are: %s" park-data))

        (= found-keyword "sports")
          (if (= true park-data)
            (println (format ">Chatbot: There is a sport field in Bertramka."))
            (println (format ">Chatbot: Unfortunately, there is no sport field in Bertramka.")))

        (= found-keyword "dogs")
          (if (= true park-data)
            (println ">Chatbot: You can enter Bertramka with your dogs")
            (println ">Chatbot:  Unfortunately, you can't enter Bertramka with your dogs")))

      (println (format ">Chatbot: There is no information provided about %s " found-keyword)))))
