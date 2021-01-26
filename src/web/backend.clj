(ns web.backend
  (:require
    [ring.adapter.jetty :refer [run-jetty]]
    [clojure.string :as str]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.session :refer [wrap-session]]
    [ring.util.response :refer [response]]
    [chatbot.identify_keyword :refer [keyword-response-main]]
    [chatbot.parse :refer [parse-input]]
    [chatbot.bot_utils :as bot]
    [chatbot.park_utils :as park]
    [web.db :as db]
    [web.pages :as pages]))


(def list-of-park-uris (map #(str "/" (name %)) park/park-name-keywords))

(defn chatbot-respond!
  "Generates a response to the user and inserts the result in the chosen
  database"
  [session user-input]
  (let [parsed-input (keyword-response-main (parse-input user-input))
        park-string
        (db/find-one (:id session))
        ret-str
        (str park-string "<p class=\"bot-msg\">"
             (if parsed-input (park/find-park-data parsed-input)
               (rand-nth bot/possible-error-messages)) "</p>")]
    (db/upsert (:id session) ret-str)))

(defn set-park!
  "Sets the global park-name variable from park_utils
  to the user selected park"
  [route-park-name]
  (when-not (= route-park-name @park/park-name)
    (dosync (ref-set park/park-name route-park-name))))

(defn routes-handler
  "Inspects a route and performs according action"
  [uri params session]
  (cond
    (= uri "/")
    (response (pages/index))

    (some #(= uri %) list-of-park-uris)
    (do
      (set-park! (str/replace uri #"/" ""))
      (when (get params "input")
        ; Insert a user in db if they aren't there yet
        (when (and (= @db/db-type :mongo)
                   (nil? (db/find-one (:id session))))
          (db/insert (:id session) ""))
        (let [park-string
              (db/find-one (:id session))
              string (if-not (nil? park-string)
                       (str park-string
                            "<p class=\"human-msg\">"
                            (get params "input") "</p>")
                       (str "<p class=\"human-msg\">"
                            (get params "input") "</p>"))]
          (db/upsert (:id session) string))
        (chatbot-respond! session (get params "input")))
      (response
        (pages/chatbot-page
          (db/find-one (:id session))
          uri)))

    :else
    (response (str "<h1>Route not found</h1>"
                   "<h2><a href="\/">Home</a></h2>"))))

(defn ensure-session-id
  "Creates a new and unique session id"
  [session]
  (if (nil? (:id session))
    (loop [id (rand-int 100000000)]
      ; check if the id already exists
      (if (nil? (db/find-one (:id session)))
        (assoc session :id id)
        (recur (rand-int 100000000))))
    session))

(def request-handler "Handles current request and assigns session id"
  (-> (fn [& args]
        (let [session (get (first args) :session)
              uri (get (first args) :uri)
              params (get (first args) :params)
              session+id (ensure-session-id session)
              server-response (routes-handler uri params session)]
          (-> server-response
              (assoc :session session+id))))
      (wrap-resource "public")
      (wrap-params {:encoding "UTF-8"})
      (wrap-session {:cookie-attrs {:max-age 3600}})))

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

(defn run-backend!
  "Starts the server on chosen or default port and
  connects to the chosen or default database"
  [args]
  (set-up-db! args)
  (run-jetty request-handler {:port (get-port-number args)}))
