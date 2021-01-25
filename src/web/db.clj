(ns web.db
  (:require
    [environ.core :refer [env]]
    [monger.core :as mg]
    [monger.collection :as mc]
    [chatbot.park_utils :as park]))


(def chat-map "Map that is used to store data when a database was not
              selected"
  (ref (hash-map)))

(def db-type "Type of database used, can be :map or :mongo"
  (ref :map))
(def db "Selected MongoDB database"
  (ref nil))

(def coll "Selected MongoDB collection"
  (ref nil))

(defn find-one
  "Find an entry in MongoDB based on the user's id"
  [user-id]
  (if (= @db-type :mongo)
  (get
    (mc/find-one-as-map
      @db @coll
      {:user_id user-id})
    (keyword @park/park-name))
  (get
    @chat-map
    (keyword (str user-id)))))

(defn insert
  "Insert an entry in MongoDB based on the user's id"
  [user-id val-to-insert]
          (mc/insert @db @coll
                     {:user_id user-id
                      (keyword @park/park-name) val-to-insert}))

(defn upsert
  "Upsert an entry in MongoDB based on the user's id"
  [user-id val-to-upsert]
  (if (= @db-type :mongo)
    (mc/update @db @coll {:user_id user-id}
               (assoc (mc/find-one-as-map @db @coll
                                          {:user_id user-id})
                      (keyword @park/park-name) val-to-upsert)
               {:upsert true})
    (dosync
      (ref-set chat-map
                     (assoc @chat-map
                          (keyword (str user-id))
                            val-to-upsert)))))

(defn connect-to-db!
  "Connects to MongoDB. Credentials must be supplied through environ
  (https://github.com/weavejester/environ)"
  []
  (let [^MongoOptions opts (mg/mongo-options '(:threads-allowed-to-block-for-connection-multiplier 300))
        ^ServerAddress sa  (mg/server-address "127.0.0.1" 27017)
        conn               (mg/connect sa opts)]
    (dosync
      (ref-set
        db
        (mg/get-db conn (env :database)))
      (ref-set
        coll
        (env :collection)))))
