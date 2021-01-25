(ns chatbot.user_utils
  (:require [chatbot.bot_utils :as bot]
            [clojure.string :as str]))

; User prompt which can be later changed to user's actual name
; after asking for it in the conversation
(def user-prompt "User prompt in the REPL"
  (ref "User> "))

(defn get-user-input
  "Prints the user prompt and returns the user's input"
  []
  (print @user-prompt)
  (flush)
  (str/trimr (read-line)))

(defn set-user-prompt!
  "Sets the username"
  []
  (loop []
    (bot/bot-print! "Input your username or type 'skip'.")
    (print @user-prompt)
    (flush)
    (let [ans (str/trim (read-line))]
      (cond
        (> (count (str/split ans #" ")) 1)
        (do (bot/bot-print! "Use a 1 word username")
            (recur))

        (> (count ans) 15)
        (do (bot/bot-print! "Choose a shorter username (max 15 characters)")
            (recur))

        (= (count ans) 0)
        (do (bot/bot-print! "Empty input")
            (recur))

        (= "skip" (str/lower-case ans))
        (bot/bot-print! "Username selection skipped.")

        :else (dosync
                (ref-set user-prompt (str ans "> "))
                (bot/bot-print! (str "User name changed to " ans))))))
  nil)

(defn offer-help-to-user
  []
  (bot/bot-print! (str "Too many unrecognized sentences! Do you want me to print"
                   " out the help instructions? (yes/no)"))
  (let [user-input (str/lower-case (get-user-input))]
   (cond

    (= "yes" user-input)
    (bot/help-function)

    (= "no" user-input)
    (bot/bot-print! "Okay, try again.")

    :else
    (do
      (bot/bot-print! "Wrong input!")
      (offer-help-to-user)))))

(defn handle-unrecognized-sentence
  "Return an error message and increment error counter.
  if there were 3 error messages already printed out, offers help"
  []
  (dosync (ref-set bot/unrecognized-sentences-counter
                   (inc @bot/unrecognized-sentences-counter)))
  (if (> @bot/unrecognized-sentences-counter 3)
    (offer-help-to-user)
    (bot/bot-print!
      (rand-nth bot/possible-error-messages) :error-msg? true)))
