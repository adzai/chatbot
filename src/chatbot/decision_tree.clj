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
  (cond
    (nil? (first lst-of-nodes))
    nil
    (some #(= (:answer-to-previous (first lst-of-nodes)) %) user-response)
       (first lst-of-nodes)
    :else
    (recur user-response (rest lst-of-nodes))))

; Example of constructing a decision tree about birds
(def bird-decision-tree (make-tree))
;bird seen or not?
(tree-insert! bird-decision-tree nil
              "Have you seen bird in the park? Type 'yes' or 'no'.")
(tree-insert! bird-decision-tree "no" "Okay,bye"
            :attach-to
              "Have you seen bird in the park? Type 'yes' or 'no'.")
;dark or light colored bird
(tree-insert! bird-decision-tree "yes"
              "Was the bird dark or light colored? Type 'dark' or 'light'."
            :attach-to
              "Have you seen bird in the park? Type 'yes' or 'no'.")

;dark colored bird - black?
(tree-insert! bird-decision-tree "dark"
              "Was the bird black? Type 'yes' or 'no'."
            :attach-to
              "Was the bird dark or light colored? Type 'dark' or 'light'.")
;dark or light colored beak of the black bird
(tree-insert! bird-decision-tree "yes"
              (str "Was black bird's beak dark or light colored? "
                   "Type 'dark' or 'light'.")
            :attach-to
              "Was the bird black? Type 'yes' or 'no'.")
;decision made for black bird with dark beak.
(tree-insert! bird-decision-tree "dark"
              (str "It was probably a crow. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              (str "Was black bird's beak dark or light colored? "
                   "Type 'dark' or 'light'."))
;decision made for black bird with light beak.
(tree-insert! bird-decision-tree "light"
              (str "It was probably a starling. "
                   "For more information about birds, "
                   "please type - 'bird' again." )
            :attach-to
              (str "Was black bird's beak dark or light colored? "
                   "Type 'dark' or 'light'."))
;dark colored bird - brown?
(tree-insert! bird-decision-tree "no"
              "Was the bird brown? Type 'yes' or 'no'."
            :attach-to
              "Was the bird black? Type 'yes' or 'no'.")
;dark or light colored beak of the brown bird
(tree-insert! bird-decision-tree "yes"
              (str "Was brown bird's beak dark or light colored? "
                    "Type 'dark' or 'light'.")
            :attach-to "Was the bird brown? Type 'yes' or 'no'.")
;decision made for brown bird with dark beak.
(tree-insert! bird-decision-tree "dark"
              (str "It could have been a european dipper. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              (str "Was brown bird's beak dark or light colored? "
                    "Type 'dark' or 'light'."))
;decision made for brown bird with light beak.
(tree-insert! bird-decision-tree "light"
              (str "It was probably a european greenfinch. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
             (str "Was brown bird's beak dark or light colored? "
                    "Type 'dark' or 'light'."))
;dark colored bird - grey?
(tree-insert! bird-decision-tree "no"
              "Was the bird grey? Type 'yes' or 'no'."
            :attach-to
              "Was the bird brown? Type 'yes' or 'no'.")
;dark or light colored beak of the grey bird
(tree-insert! bird-decision-tree "yes"
              (str "Was grey bird's beak dark or light colored? "
                    "Type 'dark' or 'light'.")
            :attach-to
              "Was the bird grey? Type 'yes' or 'no'.")
;decision made for grey bird with dark beak.
(tree-insert! bird-decision-tree "dark"
              (str "I think it was a grey catbird. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              (str "Was grey bird's beak dark or light colored? "
                    "Type 'dark' or 'light'."))
;decision made for grey bird with light beak.
(tree-insert! bird-decision-tree "light"
              (str "It was probably a nuthatch. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              (str "Was grey bird's beak dark or light colored? "
                    "Type 'dark' or 'light'."))


;light colored bird - red?
(tree-insert! bird-decision-tree "light"
              "Was the bird red? Type 'yes' or 'no'."
            :attach-to
              "Was the bird dark or light colored? Type 'dark' or 'light'.")
;dark or light colored beak of the red bird
(tree-insert! bird-decision-tree "yes"
              (str "Was red bird's beak dark or light colored? "
                   "Type 'dark' or 'light'.")
            :attach-to
              "Was the bird red? Type 'yes' or 'no'.")
;decision made for red bird with dark beak.
(tree-insert! bird-decision-tree "dark"
              (str "It was probably a robin. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
             (str "Was red bird's beak dark or light colored? "
                  "Type 'dark' or 'light'."))
;decision made for red bird with light beak.
(tree-insert! bird-decision-tree "light"
              (str "It could have been a middle spotted woodpecker. "
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              (str "Was red bird's beak dark or light colored? "
                   "Type 'dark' or 'light'."))
;light colored bird - white?
(tree-insert! bird-decision-tree "no"
              "Was the bird white? Type 'yes' or 'no'."
            :attach-to
              "Was the bird red? Type 'yes' or 'no'.")
;dark or light colored beak of the white bird
(tree-insert! bird-decision-tree "yes"
              (str "Was white bird's beak dark or light colored? "
                   "Type 'dark' or 'light'.")
            :attach-to
              "Was the bird white? Type 'yes' or 'no'.")
;decision made for white bird with dark beak.
(tree-insert! bird-decision-tree "dark"
             (str "I assume it was a marsh tit. "
                  "For more information about birds, "
                  "please type - 'bird' again.")
              :attach-to
              (str "Was white bird's beak dark or light colored? "
                   "Type 'dark' or 'light'."))
;decision made for white bird with light beak.
(tree-insert! bird-decision-tree "light"
              (str "It was probably a collared dove."
                   "For more information about birds, "
                   "please type - 'bird' again.")
            :attach-to
              (str "Was white bird's beak dark or light colored? "
                   "Type 'dark' or 'light'."))


; Example of how the decision tree traversal with user input might look like
 (defn questions-loop-helper
   [node]
   (bot-print! (:response-to-user node))
   (when-not (empty? @(:children node))
     (let [user-input (parse-input (get-user-input))
          next-node (find-node-response user-input @(:children node))]
       (if (nil? next-node)
          (do
            (bot-print!  "Couldn't find a match for your answer.")
            (bot-print!  (str
                          "For more information about birds,"
                          "type - 'bird'."))
            (bot-print!  (str
                          "Otherwise, you can continue to get information "
                          "about the current park "
                          "or change the park by typing keyword - 'park'.")))
         (recur next-node)))))

 (defn questions-loop
   [tree]
   (questions-loop-helper @(:root tree)))
