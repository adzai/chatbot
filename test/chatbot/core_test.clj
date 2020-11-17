(ns chatbot.core-test
  (:require [clojure.test :refer :all]
            [chatbot.core :refer :all]
            [clojure.java.io :as io]
            [chatbot.get_data :refer [create-data]]
            [chatbot.levenshtein :refer [similarity]]
            [chatbot.identify_keyword :refer :all]))

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

(deftest keyword-response-vector-valid-test
  (testing "Testing keyword identifier function with valid input"
    (is 
      (= "wc" (keyword-response-vector (vector "wc" "restroom" "bath") "restroom")))))

(deftest keyword-response-vector-invalid-test
  (testing "Testing keyword identifier function with invalid input"
    (is 
      (= false (keyword-response-vector (first (vals synonyms-map)) "something")))))

(deftest keyword-response-list-valid-test
  (testing "Testing keyword identifier with list of vectors and invalid input"
    (is
      (= "transportation" (keyword-response-list (vals synonyms-map) "metro")))))

(deftest keyword-response-list-invalid-test
  (testing "Testing keyword identifier with list of vectors and invalid input"
    (is
      (= false (keyword-response-list (vals synonyms-map) "something")))))

(deftest keyword-response-main-valid-test
  (testing "Testing the keyword identifier function with valid input"
    (is
      (= "biking" (keyword-response-main "bicycle")))))

(deftest keyord-response-main-invalid-test
  (testing "Testing the keyword identifier function with invalid input"
    (is
      (= false (keyword-response-main "Something")))))