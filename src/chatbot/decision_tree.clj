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
    (bot-print! "Target response to user was not found")
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
(tree-insert! bird-decision-tree nil 
              "What color of bird did you see in the park?")
(tree-insert! bird-decision-tree "black" "What color was the beak?"
             :attach-to "What color of bird did you see in the park?")
(tree-insert! bird-decision-tree "brown" "It could have been a sparrow."
             :attach-to "What color of bird did you see in the park?")
(tree-insert! bird-decision-tree "black" "It was probably a crow."
              :attach-to "What color was the beak?")
(tree-insert! bird-decision-tree "brown" "It was probably a magpie."
              :attach-to "What color was the beak?")

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
