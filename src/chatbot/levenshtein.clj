; https://en.wikipedia.org/wiki/Levenshtein_distance
(ns chatbot.levenshtein)

(defn nextcol
  "Determines next col"
  [char1 char2 prev-row current-row position]
  (if (= char1 char2)
    (nth prev-row (dec position))
    (inc (min
           (nth prev-row (dec position))
           (nth prev-row position)
           (last current-row)))))

(defn nextrow
  "Determines next row"
  [char1 str2 prev-row current-row]
  (let [char2 (first str2)
        pos (count current-row)]
    (if (= pos (count prev-row))
      current-row
      (recur
        char1
        (rest str2)
        prev-row
        (conj current-row (nextcol char1 char2 prev-row current-row pos))))))

(defn levenshtein
  "Calculates the number of edits to make two strings the same"
  ([str1 str2]
   (let [start-row (vec (range (inc (count str2))))]
     (levenshtein 1 start-row str1 str2)))
  ([row-num prev-row str1 str2]
   (let [next-row (nextrow (first str1) str2 prev-row (vector row-num))]
     (if (empty? (rest str1))
       (last next-row)
       (recur (inc row-num) next-row (rest str1) str2)))))

(defn similarity
  "Calculates similarity % of 2 strings"
  [s1 s2]
  (let [ld (levenshtein s1 s2)
        len1 (count s1)
        len2 (count s2)]
    (double (- 1 (/ ld (max len1 len2))))))
