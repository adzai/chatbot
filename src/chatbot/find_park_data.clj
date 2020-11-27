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
              "%sYou can find %s in Bertramka."
              bot/bot-prompt user-keyword)
            (format
              "%sUnfortunately, there is no %s in Bertramka."
              bot/bot-prompt user-keyword))

        (some #(= found-keyword %) [:biking :skating :skiing])
          (if (= true park-data)
            (format
              "%s%s is possible in Bertramka."
              bot/bot-prompt user-keyword)
            (format
              "%sUnfortunately, %s is not possible in Bertramka."
              bot/bot-prompt user-keyword))

        (= found-keyword :attractions)
          (format
            "%sIn Bertramka you can find such attractions as: %s."
            bot/bot-prompt park-data)

        (= found-keyword :transportation)
          (format
            "%sYou can get to Bertramka these ways:, they are: %s."
            bot/bot-prompt park-data)

        (= found-keyword :sports)
          (if (= true park-data)
            (str bot/bot-prompt "There is a sport field in Bertramka.")
            (str bot/bot-prompt "Unfortunately, there is no sport field "
                 "in Bertramka."))

        (= found-keyword :dogs)
          (if (= true park-data)
            (str bot/bot-prompt "You can enter Bertramka with your "
                                      "dogs.")
            (str bot/bot-prompt "Unfortunately, you can't enter
                                      Bertramka with dogs.")))

      (format
        "%sThere is no information provided about %s."
        bot/bot-prompt user-keyword))))
