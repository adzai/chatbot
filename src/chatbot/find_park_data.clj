(ns chatbot.find_park_data
  (:require
    [chatbot.parse :refer [parse-json park->keyword]]
    [clojure.string :as str]))

(defn find-park-data
  "Checks what value of the keyword identified in user input is in the data
  structure containing data about the park and prints out the corresponding
  bot's answer depending on the keyword type"
  [user-keyword park-name]
  (let [found-keyword (keyword user-keyword)
        park-keyword (park->keyword park-name)
        park-info (get (parse-json "data/data-en.json") park-keyword)
        park-data? (get park-info found-keyword)]
    (if-not (nil? park-data?)
      (cond (some #(= found-keyword %) [:wc :playground :parking])
            (if park-data?
              (format
                "You can find %s in %s."
                user-keyword park-name)
              (format
                "Unfortunately, there is no %s in %s."
                user-keyword park-name))

            (some #(= found-keyword %) [:biking :skating :skiing])
            (if park-data?
              (format
                "%s is possible in %s."
                (str/capitalize user-keyword) park-name)
              (format
                "Unfortunately, %s is not possible in %s."
                user-keyword park-name))

            (= found-keyword :attractions)
            (format
              "In %s you can find such attractions as: %s."
              park-name park-data?)

            (= found-keyword :transportation)
            (format
              "You can get to %s these ways:, they are: %s."
              park-name park-data?)

            (= found-keyword :sports)
            (if park-data?
              (str "There is a sport field in " park-name ".")
              (str "Unfortunately, there is no sport field "
                   "in " park-name "."))

            (= found-keyword :dogs)
            (if park-data?
              (str "You can enter " park-name " with your "
                   "dogs.")
              (str "Unfortunately, you can't enter
                   " park-name " with dogs.")))

      (format
        "There is no information provided about %s in %s."
        user-keyword park-name))))

(defn park-history [park-name]
  (let [full-info-map (parse-json "data/park-history.json")
        park-keyword (park->keyword park-name)]
    (get full-info-map park-keyword)))
