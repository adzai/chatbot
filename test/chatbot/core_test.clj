(ns chatbot.core-test
  (:require [clojure.test :refer :all]
            [chatbot.parse :refer :all]
            [clojure.java.io :as io]
            [chatbot.get_data :refer [create-data]]
            [chatbot.levenshtein :refer [similarity]]
            [chatbot.bot_utils :refer :all]
            [chatbot.identify_keyword :refer :all]
            [chatbot.find_park_data :refer [find-park-data]]))

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
      (= "wc"
         (keyword-response-vector
           (vector "wc" "restroom" "bath") (parse-input "restroom"))))))

(deftest keyword-response-vector-invalid-test
  (testing "Testing keyword identifier function with invalid input"
    (is
      (= false
         (keyword-response-vector
           (first (vals synonyms-map)) (parse-input "something"))))))

(deftest keyword-response-list-invalid-test
  (testing "Testing keyword identifier with list of vectors and invalid input"
    (is
      (= false
         (keyword-response-list (vals synonyms-map) "something")))))


(deftest keyword-response-list-valid-test
  (testing "Testing keyword identifier with list of vectors and invalid input"
    (is
      (= "transportation"
         (keyword-response-list (vals synonyms-map) "metro")))))

(deftest keyword-response-main-valid-test
  (testing "Testing the keyword identifier function with valid input"
    (is
      (= "biking"
         (keyword-response-main "bicycle")))))

(deftest keyword-response-main-test
  (testing "Testing the keyword identifier function with input - dog"
    (is
      (= "dogs"
         (keyword-response-main "dog")))))

(deftest keyword-response-main-invalid-test
  (testing "Testing the keyword identifier function with invalid input"
    (is
      (= false
         (keyword-response-main "Something")))))

(deftest greeting-input-not-identified-test
  (testing "Testing greeting function with the input which is not a greeting"
    (is
      (= false (greeting possible-greetings (parse-input "something"))))))

(deftest greeting-input-identified-test
  (testing "Testing greeting function with the input which is a greeting"
    (let [greeting-result (greeting possible-greetings (parse-input "hi"))]
      (is
        (= true
           (some #(= greeting-result %) responses))))))

(deftest find-park-data-test
  (testing "Testing the keyword response function with input - wc"
    (is
      (= "You can find wc in Bertramka.")
      (find-park-data "wc"))))

(deftest find-park-data-absent-keyword-test
  (testing "Testing the keyword response function with non-existent keyword"
    (is
      (= (str "There is no information provided "
              "about dogs.")
         (find-park-data "dogs")))))
