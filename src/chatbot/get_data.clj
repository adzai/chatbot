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
   {:name "Vojanovy sady"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/vojanovy_sady/index.html"
    :file "data/vojanovy-sady.html"}
   {:name "Vyšehrad"
    :url "https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/vysehrad/index.html"
    :file "data/vysehrad.html"}])

(defn download-files [parks]
  (let [header 
        "--user-agent=\"User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.89 Safari/537.36\""]
  (loop [parks parks
         park (first parks)]
    (when-not (empty? parks)
      (when-not (.exists (io/file (get park :file)))
        (shell/sh "wget" header (get park :url) "-O" (get park :file)))
      (recur (rest parks) (first (rest parks)))))
  (shutdown-agents)))

(defn extract-data [park]
  (let [website-content
        (html/html-resource (java.io.StringReader. 
                              (slurp (get park :file))))]
    (concat (map html/text (html/select website-content [:p.i_wc]))
            (map html/text (html/select website-content [:p.i_misto]))
            (map html/text (html/select website-content [:p.i_kolo]))
            (map html/text (html/select website-content [:p.i_brusle]))
            (map html/text (html/select website-content [:p.i_sport]))
            (map html/text (html/select website-content [:p.i_hriste]))
            (map html/text (html/select website-content [:p.i_mhd]))
            (map html/text (html/select website-content [:p.i_gps]))
            (map html/text (html/select website-content [:p.i_parking]))
            (map html/text (html/select website-content [:p.i_cesty]))
            (map html/text (html/select website-content [:p.i_provoz]))
            (map html/text (html/select website-content [:p.i_doba])))))

(defn get-all-data []
  (loop [parks parks
         park (first parks)
         end-lst '()]
    (if-not (empty? parks)
      (recur (rest parks) (first (rest parks))
             (cons (cons (str "Name: " (get park :name))
                         (extract-data park))
                   end-lst)) end-lst)))

(defn cleanup []
  (loop [parks parks
         park (first parks)]
    (when-not (empty? parks)
      (when (.exists (io/file (get park :file)))
        (io/delete-file (get park :file)))
      (recur (rest parks) (first (rest parks))))))

(defn write-data [data]
  (let [file "data/data-cz.txt"]
    (when-not (.exists (io/file file))
      (loop [data data
             row (first data)]
        (when-not (empty? data)
          (spit file (str row "\n") :append true)
          (recur (rest (rest data)) (first (rest data))))))))

(defn create-data
  []
  (when-not (.exists (io/file "data/data-cz.txt"))
    (.mkdir (java.io.File. "data"))
  (download-files parks)
  (let [data (get-all-data)]
    (write-data data))
    (cleanup)))
