(ns chatbot.get_data
  (:require [net.cgrand.enlive-html :as html])
  (:require [clojure.java.shell :as shell])
  (:require [clojure.java.io :as io]))

(def parks 
  [
   {:name "Bertramka"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/bertramka/index.html"
    :file "data/bertramka.html"}
   {:name "Františkánská-zahrada"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/frantiskanska_zahrada/index.html"
    :file "data/frantiskanska-zahrada.html"}
   {:name "Obora Hvězda"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/hvezda/index.html"
    :file "data/obora-hvezda.html"}
   {:name "Kampa"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/kampa/index.html"
    :file "data/kampa.html"}
   {:name "Kinského zahrada"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/kinskeho_zahrada/index.html"
    :file "data/kinskeho-zahrada.html"}
   {:name "Klamovka"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/klamovka/index.html"
    :file "data/klamovka.html"}
   {:name "Ladronka"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/ladronka/index.html"
    :file "data/ladronka.html"}
   {:name "Letná"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/letna/index.html"
    :file "data/letna.html"}
   {:name "Petřín"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/petrin/index.html"
    :file "data/petrin.html"}
   {:name "Riegrovy sady"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/riegrovy_sady/index.html"
    :file "data/riegrovy-sady.html"}
   {:name "Stromovka"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/stromovka/index.html"
    :file "data/stromovka.html"}
   {:name "Vyšehrad"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/vysehrad/index.html"
    :file "data/vysehrad.html"}])

(defn download-files []
  (println "Downloading pages")
  (let [header 
        "--user-agent=\"User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.89 Safari/537.36\""]
    (loop [parks parks
           park (first parks)]
      (when-not (empty? parks)
        (when-not (.exists (io/file (get park :file)))
          (shell/sh "wget" header (get park :url) "-O" (get park :file)))
        (recur (rest parks) (first (rest parks)))))
    (shutdown-agents)))

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

(defn extract-data [park]
  (println "Extracting data")
  (let [website-content
        (html/html-resource (java.io.StringReader. 
                              (slurp (get park :file))))]
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

(defn get-all-data []
  (loop [parks parks
         park (first parks)
         end-lst ""]
    (if-not (empty? parks)
      (recur (rest parks) (first (rest parks))
             (str end-lst "\"" (get park :name) "\":\n\t"
                  (extract-data park) ",\n")) end-lst)))

(defn cleanup []
  (println "Cleaning up")
  (loop [parks parks
         park (first parks)]
    (when-not (empty? parks)
      (when (.exists (io/file (get park :file)))
        (io/delete-file (get park :file)))
      (recur (rest parks) (first (rest parks))))))

(defn write-data [data]
  (let [file "data/data-cz.json"]
    (when-not (.exists (io/file file))
      (spit file (str "{" (clojure.string/join "" (drop-last (drop-last data)))
                      "}")))))

(defn create-data
  []
  (when-not (.exists (io/file "data/data-cz.json"))
    (.mkdir (java.io.File. "data"))
    (download-files)
    (let [data (get-all-data)]
      (write-data data))
    (cleanup)
    (println "Data successfully downloaded")))
