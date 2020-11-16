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

(defn parse-input [input]
  "Converts user input string into a vector of words as strings in lower case"
  (let [words (str/split input #" ")
        lower-cased-words (map str/lower-case words)]
    lower-cased-words))

(def map-synonyms {"wc"  ["wc", "restroom", "bathroom", "toilet", "lavatory"],
                   "attractions"  ["attractions", "sightseeing", "landmark", "entertainment"],
                   "dogs"  ["dogs", "pet"],
                   "biking"  ["bike", "biking", "bicycle"],
                   "skating"  ["skating", "roller", "skateboard", "rollerblade"],
                   "sports"  ["sports", "gymnastics", "exercise"]
                   "playground"  ["playground", "playing"],
                   "transportation"  ["transportation", "metro", "subway", "tram", "bus", "transport"],
                   "parking"  ["parking", "car"],
                   "opening"  ["opening", "open", "enter"]})

(def park-map {"wc" "yes",
			         "attractions" "yes",
			         "biking" "yes",
			         "skating" "no",
			         "sports" "no",
			         "playground" "no",
			         "transportation" "yes",
			         "parking" "yes"})

(defn find-park-data
  "Checks what value of the keyword identified in user input is in the data
  structure containing data about the park and prints out the corresponding
  bot's answer"
  [keyword]
  (when-not (nil?(get park-map keyword))
    (if (= "yes" (get park-map keyword))
      (println (format "You can find %s in Bertramka. " keyword))
	    (println (format "Unfortunately, there is no %s in Bertramka. " keyword)))))

(defn identifying-keyword
  "Receives a string of user input and compares each word from it with each
  vector of synonyms and in case of sufficient percent of similarity passes
  the keyword idenitfying the vector in which the synonym has been matched to
  the function which analyses the found keyword within park data"
  [input]
  (let [words (parse-input input)
 	      synonyms (vals map-synonyms)]
    (doseq [synonym synonyms]
 	    (doseq [x synonym]
 	      (let [max-similarity (apply max (for [y words] (similarity y x)))]
 		      (when (> max-similarity 0.7)
 		        (find-park-data (first synonym))))))))




(defn main-loop
  "Receives user input until a terminating keyword is met"
  []
  (println ">Chatbot: Hi!")
  (println ">Chatbot: I am your park guide. I will tell you about Bertramka park. To end the conversation, enter 'finish'. Ask your questions")
  (loop [user-input (parse-input (read-line))]
    (when-not (and (= 1 (count user-input)) (some #(= "finish" %) user-input))
      (recur (read-line)))))












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
