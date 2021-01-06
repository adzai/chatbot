(ns chatbot.decision_tree)

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
  "Helper function for inserting a node in a tree."
  [answer-to-previous response-to-user parent-response-to-user queue]
  (if (empty? queue)
    (println "Target response to user was not found")
    (let [current-node (first queue)]
      (if (= parent-response-to-user (:response-to-user current-node))
        (dosync
          (ref-set (:children current-node)
                   (cons (make-node answer-to-previous response-to-user)
                         @(:children current-node))))
        (recur answer-to-previous response-to-user parent-response-to-user
               (concat (rest queue)
                       @(:children current-node)))))))

(defn tree-insert!
 "Inserts a node in a tree.
  parent-response-to-user value is used to identify to which node the newly
  created node should be attached"
  [tree answer-to-previous response-to-user parent-response-to-user]
  (if (nil? parent-response-to-user)
    (dosync (ref-set (:root tree) (make-node answer-to-previous
                                             response-to-user)))
  (tree-insert-helper! answer-to-previous response-to-user
                      parent-response-to-user
                      (cons @(:root tree) @(:children @(:root tree))))))

(defn find-node-response
  "Returns a nodes which corresponds to the user's response or nil."
  [user-response lst-of-nodes]
  (cond
    (nil? (first lst-of-nodes))
    nil
    (= user-response (:answer-to-previous (first lst-of-nodes)))
       (first lst-of-nodes)
    :else
    (recur user-response (rest lst-of-nodes))))

; Example of constructing a decision tree about birds
; (def bird-decision-tree (make-tree))
; (tree-insert! bird-decision-tree nil "What color was the bird?" nil)
; (tree-insert! bird-decision-tree "Black" "What color was the beak?"
;              "What color was the bird?")
; (tree-insert! bird-decision-tree "Brown" "It could have been a sparrow."
;              "What color was the bird?")
; (tree-insert! bird-decision-tree "Black" "It was probably a crow."
;              "What color was the beak?")

; Example of how the decision tree traversal with user input might look like
; (defn questions-loop-helper
;   [node]
;   (println (:response-to-user node))
;   (when-not (empty? @(:children node))
;     (let [user-input (read-line)
;          next-node (find-node-response user-input @(:children node))]
;       (if (nil? next-node)
;         (println "Couldn't find a match for your answer")
;         (recur next-node)))))

; (defn questions-loop
;   [tree]
;   (questions-loop-helper @(:root tree)))
