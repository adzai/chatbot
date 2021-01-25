(ns chatbot.decision_tree
  (:require [chatbot.parse :refer [parse-input]]
            [chatbot.bot_utils :refer [bot-print!]]
            [chatbot.user_utils :refer [get-user-input]]))

(defrecord Tree [root])

(defrecord Node [answer-to-previous response-to-user children])

(defn make-tree
  "Creates a new tree."
  []
  (Tree. (ref nil)))

(defn make-node
  "Creates a new node."
  [answer-to-previous response-to-user]
  (Node. answer-to-previous response-to-user (ref '())))

(defn tree-insert-helper!
  "Helper function for inserting a node in a tree.
   The attach-to value is used to identify to which node the newly
   created node should be attached."
  [answer-to-previous response-to-user attach-to queue]
  (if (empty? queue)
    (println "Target response to user was not found")
    (let [current-node (first queue)]
      (if (= attach-to (:response-to-user current-node))
        (dosync
          (ref-set (:children current-node)
                   (cons (make-node answer-to-previous response-to-user)
                         @(:children current-node))))
        (recur answer-to-previous response-to-user attach-to
               (concat (rest queue)
                       @(:children current-node)))))))

(defn tree-insert!
  "Inserts a node in a tree."
  [tree answer-to-previous response-to-user
   & {:keys [attach-to] :or {attach-to false}}]
  (if attach-to
    (tree-insert-helper! answer-to-previous response-to-user
                         attach-to
                         (cons @(:root tree) @(:children @(:root tree))))
    (dosync (ref-set (:root tree) (make-node answer-to-previous
                                             response-to-user)))))

(defn find-node-response
  "Returns a nodes which corresponds to the user's response or nil."
  [user-response lst-of-nodes]
  (let [node (first lst-of-nodes)]
  (cond
    (nil? node)
    nil
    (some #(= (:answer-to-previous node) %) user-response)
    node
    :else
    (recur user-response (rest lst-of-nodes)))))

(defn bird-helper
  "Describes the functionalities of bird identification mode."
  []
  (bot-print! (str
               "The bot can help the user to identify "
               "the birds of Prague parks."))
  (bot-print! (str
               "Bot asks user questions about the"
               "specific characteritics of birds."))
  (bot-print! (str
               "Based on the recieved inputs, "
               "chatbot will identifiy the bird species."))
  (bot-print! (str
               "The user must give the specific inputs to the asked questions"
               ", for example, 'yes'/'no' or 'dark'/'light'." ))
  (bot-print! (str
               "If the user-input is obscure to the bot, "
               "it will raise an error and ask user "
               "for more specific response."))
  (bot-print! (str
               "Once the bird is identified, the bot goes to the normal mode "
               "and recieves user input about the chosen park."))
  (bot-print! (str
               "If the bird is not identified, the bot informs the user "
               "that the match is not found and goes to the normal mode."))
  (bot-print! (str
               "The user can exit the bird identifier mode by typing "
               "keyword - 'helper'."))
  (bot-print! (str
               "In order to continue bird identification, type the "
               "keyword - 'bird' once again.")))

(defn questions-loop-helper
  "Helper for the 'questions-loop' function. Recieves a user input. "
  [node]
  (if (empty? @(:children node))
    (bot-print! (:response-to-user node))
    (do (bot-print! (:response-to-user node))
        (let [user-input (parse-input (get-user-input))
              next-node (find-node-response user-input @(:children node))
              bird-help? (= '("helper") user-input)
              bird-exit? (= '("exit") user-input)]
          (cond
            bird-exit?
            (do
              (bot-print!
               "Now, you can ask questions about the chosen park.")
              (bot-print!
               "To change the current park, type - 'park'."))

            bird-help?
            (bird-helper)

            (nil? next-node)
            (do
              (bot-print!
               (str
                "Please, give a specific answer, for example, "
                "'yes'/'no' or 'dark'/'light'. "))
              (bot-print!
               "If you need further help, type - 'helper'.")
              (questions-loop-helper node))

            :else (recur next-node))))))

(defn questions-loop
  "Main loop for the bird identification mode."
  [tree]
  (questions-loop-helper @(:root tree)))

(def bird-decision-tree "Decision tree for the chosen domain - birds."
  (make-tree))

;bird seen or not?
(tree-insert! bird-decision-tree nil
              "Have you seen a bird in the park?")
(tree-insert! bird-decision-tree "no"
               (str
                "For more information about birds, type - 'bird'."
                "Otherwise, you will continue getting information"
                "about the chosen park.")
            :attach-to
              "Have you seen a bird in the park?")

;dark or light colored bird
(tree-insert! bird-decision-tree "yes"
              "Was the bird dark or light colored?"
            :attach-to
              "Have you seen a bird in the park?")

;dark colored bird - black?
(tree-insert! bird-decision-tree "dark"
              "Was the bird black?"
            :attach-to
              "Was the bird dark or light colored?")
;dark or light colored beak of the black bird
(tree-insert! bird-decision-tree "yes"
              "Was black bird's beak dark or light colored?"
            :attach-to
              "Was the bird black?")
;decision made for black bird with dark beak.
(tree-insert! bird-decision-tree "dark"
              (str "It was probably a crow. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              "Was black bird's beak dark or light colored?")
;decision made for black bird with light beak.
(tree-insert! bird-decision-tree "light"
              (str "It was probably a starling. "
                   "For more information about birds, "
                   "please type - 'bird' again." )
            :attach-to
              "Was black bird's beak dark or light colored?")

;dark colored bird - brown?
(tree-insert! bird-decision-tree "no"
              "Was the bird brown?"
            :attach-to
              "Was the bird black?")
;dark or light colored beak of the brown bird
(tree-insert! bird-decision-tree "yes"
              "Was brown bird's beak dark or light colored?"
            :attach-to "Was the bird brown?")
;decision made for brown bird with dark beak.
(tree-insert! bird-decision-tree "dark"
              (str "It could have been a european dipper. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              "Was brown bird's beak dark or light colored?")
;decision made for brown bird with light beak.
(tree-insert! bird-decision-tree "light"
              (str "It was probably a european greenfinch. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
             "Was brown bird's beak dark or light colored?")

;dark colored bird - grey?
(tree-insert! bird-decision-tree "no"
              "Was the bird grey?"
            :attach-to
              "Was the bird brown?")
;dark or light colored beak of the grey bird
(tree-insert! bird-decision-tree "yes"
              "Was grey bird's beak dark or light colored?"
            :attach-to
              "Was the bird grey?")
;decision made for grey bird with dark beak.
(tree-insert! bird-decision-tree "dark"
              (str "I think it was a grey catbird. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              "Was grey bird's beak dark or light colored?")
;decision made for grey bird with light beak.
(tree-insert! bird-decision-tree "light"
              (str "It was probably a nuthatch. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              "Was grey bird's beak dark or light colored?")

;light colored bird - red?
(tree-insert! bird-decision-tree "light"
              "Was the bird red?"
            :attach-to
              "Was the bird dark or light colored?")
;dark or light colored beak of the red bird
(tree-insert! bird-decision-tree "yes"
              "Was red bird's beak dark or light colored?"
            :attach-to
              "Was the bird red?")
;decision made for red bird with dark beak.
(tree-insert! bird-decision-tree "dark"
              (str "It was probably a robin. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              "Was red bird's beak dark or light colored?")
;decision made for red bird with light beak.
(tree-insert! bird-decision-tree "light"
              (str "It could have been a middle spotted woodpecker. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              "Was red bird's beak dark or light colored?")

;light colored bird - white?
(tree-insert! bird-decision-tree "no"
              "Was the bird white?"
            :attach-to
              "Was the bird red?")
;dark or light colored beak of the white bird
(tree-insert! bird-decision-tree "yes"
              "Was white bird's beak dark or light colored?"
            :attach-to
              "Was the bird white?")
;decision made for white bird with dark beak.
(tree-insert! bird-decision-tree "dark"
              (str "I assume it was a marsh tit. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              "Was white bird's beak dark or light colored?")
;decision made for white bird with light beak.
(tree-insert! bird-decision-tree "light"
              (str "It was probably a collared dove."
                   "For more information about birds, "
                   "please type - 'bird' again.")
             :attach-to
              "Was white bird's beak dark or light colored?")

;match not found for dark colored bird, i.e.,
;the seen bird is not black, brown or grey.
(tree-insert! bird-decision-tree "no"
              (str "Unfortunately, the match was not found."
                   "For more information about birds, type - 'bird'.")
              :attach-to
               "Was the bird grey?")
;match not found for light colored bird, i.e.,
; the seen bird is not red or white.
(tree-insert! bird-decision-tree "no"
              (str "Unfortunately, the match was not found. "
                   "For more information about birds, type - 'bird'.")
              :attach-to
               "Was the bird white?")