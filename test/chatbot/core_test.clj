(ns chatbot.core-test
  (:require [clojure.test :refer :all]
            [chatbot.core :refer :all]
            [clojure.java.io :as io]
            [chatbot.get_data :refer [create-data]]
            [chatbot.levenshtein :refer [similarity]]))

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

(deftest find-park-data-test
  (testing "Testing the keyword response function with input - wc"
    (is
      (= ">Chatbot: Unfortunately, there is no wc in Bertramka."
         (find-park-data "wc")))))

(deftest find-park-data-absent-keyword-test
  (testing "Testing the keyword response function with non-existent keyword"
    (is
      (= ">Chatbot: There is no information provided about dogs."
         (find-park-data "dogs")))))

