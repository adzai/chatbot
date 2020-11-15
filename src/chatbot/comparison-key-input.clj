(require '[clojure.string :as str])

                                
(defn nextcol
  "Determines next col"
  [char1 char2 prev-row current-row position]
  (if (= char1 char2)
    (nth prev-row (dec position))
    (inc (min
          (nth prev-row (dec position))
          (nth prev-row position)
          (last current-row)))))

(defn nextrow
  "Determines next row"
  [char1 str2 prev-row current-row]
  (let [char2 (first str2)
        pos (count current-row)]
    (if (= pos (count prev-row))
      current-row
      (recur
       char1
       (rest str2)
       prev-row
       (conj current-row (nextcol char1 char2 prev-row current-row pos))))))

(defn levenshtein
  "Calculates the number of edits to make two strings the same"
  ([str1 str2]
   (let [start-row (vec (range (inc (count str2))))]
     (levenshtein 1 start-row str1 str2)))
  ([row-num prev-row str1 str2]
   (let [next-row (nextrow (first str1) str2 prev-row (vector row-num))]
     (if (empty? (rest str1))
       (last next-row)
       (recur (inc row-num) next-row (rest str1) str2)))))

(defn similarity
  "Calculates similarity % of 2 strings"
  [s1 s2]
  (let [ld (levenshtein s1 s2)
        len1 (count s1)
        len2 (count s2)]
    (double (- 1 (/ ld (max len1 len2))))))

                             
(def park-map {"wc" true,
 "attractions" "cultural monument, classical music concerts, social events, W. A. Mozart Museum",
 "biking" true,
 "skating" false,
 "sports" false,
 "playground" false,
 "transportation" "trams No. 4, 7, 9, 10, 58, 59.",
 "parking" true})

(defn find-park-data
  "Checks what value of the keyword identified in user input is in the data
  structure containing data about the park and prints out the corresponding
  bot's answer depending on the keyword type"
  [keyword]
    (cond
      (some #(= keyword %) ["wc" "playground" "parking"])
      (if (= true (get park-map keyword))
        (println (format ">Chatbot: You can find %s in Bertramka. " keyword))
        (println (format ">Chatbot: Unfortunately, there is no %s in Bertramka. " keyword)))

      (some #(= keyword %) ["biking" "skating"])
      (if (= true (get park-map keyword))
        (println (format ">Chatbot: %s is possible in Bertramka. " keyword))
        (println (format ">Chatbot: Unfortunately, %s is not possible in Bertramka. " keyword)))

      (= keyword "attractions")
      (println (format ">Chatbot: In Bertramka you can find multiple attractions, some are: %s" (get park-map keyword)))

      (= keyword "transportation")
      (println (format ">Chatbot: You can get to Bertramka these ways:, they are: %s" (get park-map keyword)))
      
      (= keyword "sports")
      (if (= true (get park-map keyword))
        (println ">Chatbot: There is a sport field in Bertramka. ")
        (println ">Chatbot: Unfortunately, there is no sport field in Bertramka. "))

      (some #(= keyword %) ["dogs" "skiing"])
      (println (format "> Chatbot: No information provided about %s in Bertramka park" keyword))))


                            


(def synonyms-map {"wc"  ["wc", "restroom", "bathroom", "toilet", "lavatory"],
                   "attractions"  ["attractions", "sightseeing", "landmark", "entertainment"],
                   "dogs"  ["dogs", "pet"],
                   "biking"  ["biking", "bike", "bicycle"],
                   "skiing"  ["skiing"],
                   "skating"  ["skating", "roller", "skateboard", "rollerblade"],
                   "sports"  ["sports", "gymnastics", "exercise"],
                   "playground"  ["playground", "playing"],
                   "transportation"  ["transportation", "metro", "subway", "tram", "bus", "transport"],
                   "parking"  ["parking", "car"]})

                       
(defn parse-input [input]
  (let [words (str/split input #" ")
        lower-cased-words (map str/lower-case words)]
    lower-cased-words))




(defn keyword-response-vector  
  "Takes the vector of synonyms, identifies keyword and prints the corresponding response. If the keyword is not identified, function returns true"
  [synonyms-vector input]
  (let [words (parse-input input)
        synonyms-lst synonyms-vector]
  (loop [synonyms-vec synonyms-vector]
    (let [synonym (first synonyms-vec)]
      (if (not(nil? synonym)) 
        (let [max-similarity (apply max (for [y words] (similarity y (first synonyms-vec))))]
          (if (> max-similarity 0.75)
            (find-park-data (first synonyms-lst))
            (recur (rest synonyms-vec))))
        true)))))

(defn keyword-response-list
  "Takes the list of vectors containing synonyms and calls keyword-response function on the first vector. If the keyword is not found in any of the vectors, then returns true "
  [synonyms-lst input]
  (if (not (empty? synonyms-lst))
    (if (= true (keyword-response-vector (first synonyms-lst) input))
      (keyword-response-list (rest synonyms-lst) input))
    true)) 

(defn keyword-response-main 
  "Parses the user-input and retrieves values from the synonyms-map and calls keyword-response-list on the synonyms list. "
  [input]
  (let [words (parse-input input)
        synonyms (vals synonyms-map)]
    (keyword-response-list synonyms input)))



(def possible-greetings (vector "hey" "hi" "hello" "morning" "evening" "afternoon"))

(def responses (vector ">Chatbot: Hello, thanks for visiting" "Chatbot> Good to see you again" "Chatbot> Hi there, how can I help?"))

(defn greeting
  "Using similarity function, identifies if the user-input is in possible-greetings, if true function prints random greeting message, otherwise retruns true."
  [greeting-vector input]
   (let [words (parse-input input)]
     (if (not (empty? greeting-vector))
       (do
         (let [max-similarity (apply max (for [y words] (similarity y (first greeting-vector))))]
           (if (> max-similarity 0.7)
             (println (rand-nth responses))
             (greeting (rest greeting-vector) input))))
       true))) 

(def possible-error-messages 
  (vector ">Chatbot: Sorry, I didn't understand you, please try again!" 
          ">Chatbot: The Bot did not understand the question, please try in different words." 
          ">Chatbot: I am sorry, I could not understand you"))

(defn help-function 
  "Describes the functionalities of Chatbot."
  []
  (println ">Chatbot: The chatbot is designed to answer users' questions regarding Bertramka park.
           The bot provides information about following aspects of Bertramka: wc, attractions, biking, skating, sports field, playground, transportation and parking.
           Error messages are used to infrom user that asked questions are obscure to chatbot.The user can finish the conversation by typing word - finish."))

                                 
(defn main-loop
  "Receives user input until a terminating keyword is met"
  []
  (println ">Chatbot: Hi!")
  (println ">Chatbot: I am your park guide. I will tell you about Bertramka park. To end the conversation, enter 'finish'. Ask your questions")
  (loop [user-input (read-line)]
    (when-not (= "finish" user-input)
      (if (= "help" user-input)
        (help-function))
      (if (and (= true (greeting possible-greetings user-input)) (= true (keyword-response-main user-input)))
          (println (rand-nth possible-error-messages)))
      (recur (read-line)))))


    
         
    

