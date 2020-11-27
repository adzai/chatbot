(ns chatbot.find_park_data
  (:require
    [chatbot.parse :refer [parse-json]]
    [chatbot.bot_utils :as bot]))

(defn find-park-data
  "Checks what value of the keyword identified in user input is in the data
  structure containing data about the park and prints out the corresponding
  bot's answer depending on the keyword type"
  [user-keyword]
  (let [found-keyword (keyword user-keyword)
        park-data (get (parse-json "data/Bertramka.json") found-keyword)]
    (if-not (nil? park-data)
      (cond
        (some #(= found-keyword %) [:wc :playground :parking])
        (if (= true park-data)
          (format
            "You can find %s in Bertramka."
            user-keyword)
          (format
            "Unfortunately, there is no %s in Bertramka."
            user-keyword))

        (some #(= found-keyword %) [:biking :skating :skiing])
        (if (= true park-data)
          (format
            "%s is possible in Bertramka."
            user-keyword)
          (format
            "Unfortunately, %s is not possible in Bertramka."
            user-keyword))

        (= found-keyword :attractions)
        (format
          "In Bertramka you can find such attractions as: %s."
          park-data)

        (= found-keyword :transportation)
        (format
          "You can get to Bertramka these ways:, they are: %s."
          park-data)

        (= found-keyword :sports)
        (if (= true park-data)
          (str "There is a sport field in Bertramka.")
          (str "Unfortunately, there is no sport field "
               "in Bertramka."))

        (= found-keyword :dogs)
        (if (= true park-data)
          (str "You can enter Bertramka with your "
               "dogs.")
          (str "Unfortunately, you can't enter
               Bertramka with dogs.")))

      (format
        "There is no information provided about %s."
        user-keyword))))
