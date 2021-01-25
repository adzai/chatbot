(ns web.db
  (:require
    [environ.core :refer [env]]
    [monger.core :as mg]
    [monger.collection :as mc]
    [chatbot.park_utils :as park]))


(def chat-map (ref (hash-map)))


(def db-type (ref :map))
(def db (ref nil))
; (def conn (ref nil))
(def coll (ref nil))

(defn find-one
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
  [user-id val-to-insert]
          (mc/insert @db @coll
                     {:user_id user-id
                      (keyword @park/park-name) val-to-insert}))

(defn connect-to-db!
  []
  (let [conn (mg/connect)]
  (dosync
          (ref-set
            db
            (mg/get-db conn (env :database)))
          (ref-set
            coll
            (env :collection)))))

(defn upsert
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
