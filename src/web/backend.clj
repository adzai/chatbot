(ns web.backend
  (:require
    [ring.adapter.jetty :refer [run-jetty]]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.session :refer [wrap-session]]
    [ring.util.response :refer [response]]
    [chatbot.cli_utils :as cli]
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
  (let [parsed-user-input (parse-input user-input)
        greeting? (bot/greeting bot/possible-greetings parsed-user-input)
        parsed-input (keyword-response-main parsed-user-input)
        park-string
        (db/find-one (:id session))
        ret-str
        (str park-string "<p class=\"bot-msg\">"
             (cond
               greeting?
               greeting?

               parsed-input
               (park/find-park-data parsed-input)

               :else
               (rand-nth bot/possible-error-messages)) "</p>")]
    (db/upsert (:id session) ret-str)))

(defn set-park!
  "Sets the global park-name variable from park_utils
   to the user selected park"
  [route]
  (let [route-park-name (park/route-name->park route)]
  (when-not (= route-park-name @park/park-name)
    (dosync (ref-set park/park-name route-park-name)))))

(defn routes-handler
  "Inspects a route and performs according action"
  [uri params session]
  (cond
    (= uri "/")
    (response (pages/index))

    (= uri "/help")
    (response (pages/help-page))

    (= uri "/history")
    (response (pages/history-page))

    (some #(= uri %) list-of-park-uris)
    (do
      (set-park! uri)
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


(defn run-backend!
  "Starts the server on chosen or default port and
  connects to the chosen or default database"
  [args]
  (cli/set-up-db! args)
  (run-jetty request-handler {:port (cli/get-port-number args)}))
