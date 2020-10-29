(ns chatbot.get_data
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [clj-http.client :as client]))

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

(defn download-files []
  (println "Downloading pages")
  (let [urls (map #(:url %) parks)]
    (pmap #(:body (client/get %)) urls)))

(defn format-to-json [lst]
  (when-not (empty? lst)
    (let [string (clojure.string/join "" lst)
          split (clojure.string/split string #":")
          s1 (first split)
          s2-sp (clojure.string/replace (clojure.string/triml 
                                          (last split)) #"\(" "")
          s2-s (clojure.string/replace s2-sp #"\)" "")
          s2 (clojure.string/replace s2-s #"\s\s+" " ")]
      (str "\"" s1 "\": " "\"" s2 "\""))))

(defn extract-data [content]
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

(defn get-all-data [contents]
  (loop [parks parks
         contents contents
         park (first parks)
         end-str ""]
    (if-not (empty? parks)
      (recur (rest parks) (rest contents) (first (rest parks))
             (str end-str "\"" (get park :name) "\":\n\t"
                  (extract-data (first contents)) ",\n")) end-str)))

(defn write-data [data]
  (let [file "data/data-cz.json"]
    (when-not (.exists (io/file file))
      (spit file (str "{" (clojure.string/join "" (drop-last (drop-last data)))
                      "}")))))

(defn create-data []
  (when-not (.exists (io/file "data/data-cz.json"))
    (.mkdir (java.io.File. "data"))
    (let [contents (download-files)
          data (get-all-data contents)]
      (write-data data))
    (shutdown-agents)
    (println "Data successfully downloaded")))
