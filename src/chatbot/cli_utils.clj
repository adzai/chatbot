(ns chatbot.cli_utils
  (:require [web.db :as db]))

(defn start-web-server?
  "Determines whether to start the web server or not depending
  on the flags provided"
  [args]
  (if (some #(= "--web" %) args)
    true
    false))

(defn display-help?
  "Displays help and exits the program if not run with lein run but
  as an uberjar for example"
  [args]
  (when (or
          (some #(= "--help" %) args)
          (some #(= "-h" %) args))
    (println (str "Usage:\n"
                  "\t--web\tstart a webserver\n"
                  "\t--port\tstart the webserver on port number that "
                  "follows this flag\n"
                  "\t--mongo\tuse MongoDB as the database for the web server\n"
                  "\t-h --help\tthis help list\n"))
    (System/exit 0)))

(defn get-port-number
  "Determines the port number to use. Default is 3000"
  [args]
  (let [port-index (inc (.indexOf args "--port"))]
    (if (> port-index 0)
      (let [number (nth args port-index)
            parsed-number (re-find #"\d+" number)]
        (if (and (not (nil? parsed-number))
                 (> (read-string parsed-number) 1023)
                 (< (read-string parsed-number) 65536))
          (read-string parsed-number)
          (do
            (println (str "Wrong parameter following the --port flag!\n"
                          "Use a number from 1024-65535"))
            (System/exit 1))))
      3000)))

(defn set-up-db!
  "Sets db-type to mongo if chosen or defaults to using a map"
  [args]
  (if (some #(= "--mongo" %) args)
    (dosync (ref-set db/db-type :mongo)
            (db/connect-to-db!))
    (dosync (ref-set db/db-type :map))))
