(ns chatbot.core-test
  (:require [clojure.test :refer :all]
            [chatbot.core :refer :all]
            [clojure.java.io :as io]
            [chatbot.get_data :refer [create-data]]
            [chatbot.levenshtein :refer [similarity]]
            [chatbot.greet :refer :all]))

(deftest data-test
  (testing "JSON file in data folder"
    (create-data)
    (is
      (= true
         (.exists (io/file "data/data-cz.json"))))))

(deftest test-parse-function
  (testing "Testing parse function"
    (is
     (= (list "my" "name" "is" "ani")
        (parse-input "My Name Is ANI")))))

(deftest similarity-test
  (testing "Testing similarity between two strings"
    (is
        (= 0.8
           (similarity "hello" "helo")))))

(deftest parse-json-test
  (testing "Testing json-parse function"
    (is
      (map? (parse-json "data/data-en.json")))))

(deftest greeting-input-not-identified-test
  (testing "Testing greeting function with the input which is not a greeting"
    (is
      (= false (greeting possible-greetings "something")))))

(deftest greeting-input-identified-test
  (testing "Testing greeting function with the input which is a greeting"
    (is
      (= true 
         (some #(= (greeting possible-greetings "hi") %) responses)))))