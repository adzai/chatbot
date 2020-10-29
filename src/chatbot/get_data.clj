(ns chatbot.get_data
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

; Park URLs from https://www.praha.eu/jnp/cz/co_delat_v_praze/parky
(def parks 
  [
   {:name "bertramka"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/bertramka/index.html"}
   {:name "frantiskanska-zahrada"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/frantiskanska_zahrada/index.html"}
   {:name "obora-hvezda"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/hvezda/index.html"}
   {:name "kampa"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/kampa/index.html"}
   {:name "kinskeho-zahrada"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/kinskeho_zahrada/index.html"}
   {:name "klamovka"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/klamovka/index.html"}
   {:name "ladronka"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/ladronka/index.html"}
   {:name "letna"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/letna/index.html"}
   {:name "petrin"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/petrin/index.html"}
   {:name "riegrovy-sady"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/riegrovy_sady/index.html"}
   {:name "stromovka"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/stromovka/index.html"}
   {:name "vysehrad"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/vysehrad/index.html"}])

(defn download-files
  "Produces a lazy sequence of content to be downloaded which can be executed
  in parallel"
  []
  (println "Downloading pages")
  (let [urls (map #(:url %) parks)]
    ; pmap runs in parallel, so the data can be downloaded 
    ; concurrently with client/get (when it is evaluated, now it produces
    ; a lazy sequence)
    (pmap #(:body (client/get %)) urls)))

(defn format-to-json 
  "Format a list of strings to JSON format"
  [lst]
  (when-not (empty? lst)
    (let [string (str/join "" lst)
          split (str/split string #":")
          s1 (first split)
          s2-sp (str/replace (str/triml 
                               (last split)) #"\(" "")
          s2-s (str/replace s2-sp #"\)" "")
          s2 (str/replace s2-s #"\s\s+" " ")]
      (str "\"" s1 "\": " "\"" s2 "\""))))

(defn extract-data 
  "Extracts relevant data from html using CSS selectors and formats it
  to JSON"
  [content]
  (println "Extracting data")
  (let [website-content
        (html/html-resource (java.io.StringReader. content))]
    (str 
      "{" 
      (format-to-json 
        (map html/text (html/select website-content [:p.i_wc]))) ",\n\t"
      (format-to-json 
        (map html/text (html/select website-content [:p.i_misto]))) ",\n\t"
      (format-to-json 
        (map html/text (html/select website-content [:p.i_kolo])))",\n\t"
      (format-to-json 
        (map html/text (html/select website-content [:p.i_brusle]))) ",\n\t"
      (format-to-json 
        (map html/text (html/select website-content [:p.i_sport]))) ",\n\t"
      (format-to-json 
        (map html/text (html/select website-content [:p.i_hriste]))) ",\n\t"
      (format-to-json 
        (map html/text (html/select website-content [:p.i_mhd]))) ",\n\t"
      (format-to-json 
        (map html/text (html/select website-content [:p.i_parking]))) ",\n\t"
      (format-to-json 
        (map html/text (html/select website-content [:p.i_cesty]))) ",\n\t"
      (format-to-json 
        (map html/text (html/select website-content [:p.i_provoz]))) ",\n\t"
      (format-to-json 
        (map html/text (html/select website-content [:p.i_doba]))) "}")))

(defn get-all-data
  "Iterate through every html entry and returns a JSON string"
  [contents]
  (loop [parks parks
         contents contents
         park (first parks)
         end-str ""]
    (if-not (empty? parks)
      (recur (rest parks) (rest contents) (first (rest parks))
             (str end-str "\"" (get park :name) "\":\n\t"
                  (extract-data (first contents)) ",\n")) end-str)))

(defn write-data
  "Writes a JSON string to a file"
  [data]
  (let [file "data/data-cz.json"]
    (when-not (.exists (io/file file))
      (spit file (str "{" (str/join "" (drop-last (drop-last data)))
                      "}")))))

(defn create-data
  "Creates usable JSON data 
  from https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/"
  []
  (when-not (.exists (io/file "data/data-cz.json"))
    (.mkdir (java.io.File. "data"))
    (let [contents (download-files)
          data (get-all-data contents)]
      (write-data data))
    ; Shutdown threads created by pmap
    (shutdown-agents)
    (println "Data successfully downloaded")))
