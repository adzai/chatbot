(ns chatbot.user_utils
  (:require [chatbot.bot_utils :as bot]
            [clojure.string :as str]))

; User prompt which can be later changed to user's actual name
; after asking for it in the conversation
(def user-prompt (ref "User> "))

(defn get-user-input
  "Prints the user prompt and returns the user's input"
  []
  (print @user-prompt)
  (flush)
  (read-line))

(defn set-user-prompt!
  "Sets the username"
  []
  (println (str bot/bot-prompt "Input your username or type 'skip'"))
  (loop []
    (print @user-prompt)
    (flush)
    (let [ans (str/trim (read-line))]
      (cond
            (> (count (str/split ans #" ")) 1)
            (do (println "Use a 1 word username")
                (recur))
            (> (count ans) 15)
            (do (println "Choose a shorter username (max 15 characters)")
                (recur))
            (= "skip" (str/lower-case ans))
            nil
            :else (dosync
                    (ref-set user-prompt (str ans "> "))
                    (println (str bot/bot-prompt "User name changed to " ans))))))
  nil)
