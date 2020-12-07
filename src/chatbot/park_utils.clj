(ns chatbot.park_utils
  (:require [clojure.string :as str]
            [chatbot.bot_utils :refer [bot-print!]]
            [chatbot.user_utils :as chat-user]
            [chatbot.parse :refer [parse-json]]))

(def park-name (ref ""))

(def data-map (parse-json "data/data-en.json"))
(def keywords (keys data-map))

(defn park->keyword
  "Takes a park name e.g. 'Riegerovy sady' and transforms it into
  a keyword :riegerovy-sady"
  [park-name]
  (-> park-name
      (str/replace #" " "-")
      (str/lower-case)
      (keyword)))

(defn keyword->park
  "Takes a keyword  e.g. :riegerovy-sady and transforms it into
  a park name 'Riegerovy Sady'"
  [kw]
  (-> kw
      (str/replace #":" "")
      (str/replace #"-" " ")
      (str/capitalize)))

(defn user-select-park!
  "Prompt the user to select a park"
  []
  (bot-print! (str "Select a park to get info for "
                       "(type the corresponding number):"))
  (loop [i 1
         keywords keywords]
    (when-not (empty? keywords)
      (println (str i ": " (keyword->park (first keywords))))
      (recur (inc i) (rest keywords))))
  (loop []
    (let [raw-input (chat-user/get-user-input)
          input (if (= "" raw-input) false (read-string raw-input))]
      (if (and (number? input)
               (<= input (count keywords))
               (> input 0))
        (dosync (ref-set park-name
                         (keyword->park (nth keywords (dec input)))))
        (do
          (bot-print! "Enter a valid number between 1-12")
          (recur)))))
  (bot-print! (str "Ok, I will now answer your questions "
                       "about the " @park-name
                       " park!")))

(defn find-park-data
  "Checks what value of the keyword identified in user input is in the data
  structure containing data about the park and prints out the corresponding
  bot's answer depending on the keyword type"
  [user-keyword]
  (let [found-keyword (keyword user-keyword)
        park-keyword (park->keyword @park-name)
        park-info (get data-map park-keyword)
        park-data? (get park-info found-keyword)]
    (if-not (nil? park-data?)
      (cond (some #(= found-keyword %) [:wc :playground :parking])
            (if park-data?
              (format
                "You can find %s in %s."
                user-keyword @park-name)
              (format
                "Unfortunately, there is no %s in %s."
                user-keyword @park-name))

            (some #(= found-keyword %) [:biking :skating :skiing])
            (if park-data?
              (format
                "%s is possible in %s."
                (str/capitalize user-keyword) @park-name)
              (format
                "Unfortunately, %s is not possible in %s."
                user-keyword @park-name))

            (= found-keyword :attractions)
            (format
              "In %s you can find such attractions as: %s."
              @park-name park-data?)

            (= found-keyword :transportation)
            (format
              "You can get to %s these ways:, they are: %s."
              @park-name park-data?)

            (= found-keyword :sports)
            (if park-data?
              (str "There is a sport field in " @park-name ".")
              (str "Unfortunately, there is no sport field "
                   "in " @park-name "."))

            (= found-keyword :dogs)
            (if park-data?
              (str "You can enter " @park-name " with your "
                   "dogs.")
              (str "Unfortunately, you can't enter
                   " @park-name " with dogs.")))

      (format
        "There is no information provided about %s in %s."
        user-keyword @park-name))))

(defn park-history []
  (let [full-info-map (parse-json "data/park-history.json")
        park-keyword (park->keyword @park-name)]
    (get full-info-map park-keyword)))
