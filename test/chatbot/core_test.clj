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
    
