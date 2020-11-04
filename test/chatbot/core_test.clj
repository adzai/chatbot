(ns chatbot.core-test
  (:require [clojure.test :refer :all]
            [chatbot.core :refer :all]
            [clojure.java.io :as io]
            [chatbot.get_data :as data]))

(deftest data-test
  (testing "JSON file in data folder"
    (data/create-data)
    (is
      (= true 
         (.exists (io/file "data/data-cz.json"))))))

;; EXAMPLE
;; (deftest parse-test
;;   (testing "Test parsing function"
;;     (is
;;       (= ["can" "i" "ride" "a" "bike" "there" "?"]
;;          (parse-sentence "Can I ride a bike there?")))))
