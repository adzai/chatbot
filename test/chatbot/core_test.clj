(ns chatbot.core-test
  (:require [clojure.test :refer :all]
            [chatbot.parse :refer :all]
            [clojure.java.io :as io]
            [chatbot.get_data :refer [create-data]]
            [chatbot.levenshtein :refer [similarity]]
            [chatbot.bot_utils :refer :all]
            [chatbot.identify_keyword :refer :all]
            [chatbot.decision_tree :refer :all]
            [chatbot.park_utils :refer :all]))

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
         (keyword-response-list (vals synonyms-map)
                                (parse-input "something"))))))


(deftest keyword-response-list-valid-test
  (testing "Testing keyword identifier with list of vectors and invalid input"
    (is
      (= "transportation"
         (keyword-response-list (vals synonyms-map) (parse-input "metro"))))))

(deftest keyword-response-main-valid-test
  (testing "Testing the keyword identifier function with valid input"
    (is
      (= "biking"
         (keyword-response-main (parse-input "bicycle"))))))

(deftest keyword-response-main-test
  (testing "Testing the keyword identifier function with input - dog"
    (is
      (= "dogs"
         (keyword-response-main (parse-input "dog"))))))

(deftest keyword-response-main-invalid-test
  (testing "Testing the keyword identifier function with invalid input"
    (is
      (= false
         (keyword-response-main (parse-input "Something"))))))

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
    ; Set park-name manually since normally the user has to choose one when
    ; prompted
    (dosync (ref-set park-name "Bertramka"))
    (is
      (= "You can find wc in Bertramka.")
      (find-park-data "wc"))))

(deftest find-park-data-absent-keyword-test
  (testing "Testing the keyword response function with non-existent keyword"
    ; Set park-name manually since normally the user has to choose one when
    ; prompted
    (dosync (ref-set park-name "Bertramka"))
    (is
      (= (str "There is no information provided "
              "about dogs in Bertramka.")
         (find-park-data "dogs")))))

(deftest decision-tree
  (testing "Testing decision tree building"
    (let [tree (make-tree)]
      (tree-insert! tree nil "What color was the bird?")
      (tree-insert! tree "black" "What color was the beak?"
                    :attach-to "What color was the bird?")
      (is
        (= "black" (:answer-to-previous (first @(:children @(:root tree)))))))))

(deftest find-node-response-test
  (testing "Testing finding the correct node given a response"
    (let [tree (make-tree)]
      (tree-insert! tree nil "What color was the bird?")
      (tree-insert! tree "black" "What color was the beak?"
                    :attach-to "What color was the bird?")
    (is
    (= (:response-to-user
         (find-node-response ["black"] @(:children @(:root tree))))
       "What color was the beak?")))))

(deftest finish-test
  (testing "Testing the terminating keywords"
    (is
     (= true (finish? (list "exit"))))))

(deftest park->keyword-test
  (testing "Testing conversion from park to keyword"
    (is
      (= :riegrovy-sady (park->keyword "Riegrovy sady")))))

(deftest route-name->park-test
  (testing "testing conversion from route name to park"
    (is
      (= "Riegrovy sady" (route-name->park "/riegrovy-sady")))))

(deftest keyword->park-test
  (testing "testing conversion from keyword to park"
    (is
      (= "Riegrovy sady" (keyword->park :riegrovy-sady)))))
