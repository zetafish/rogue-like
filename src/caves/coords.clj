(ns caves.coords)

(def directions
  {:w [-1 0]
   :e [1 0]
   :n [0 -1]
   :s [0 1]
   :nw [-1 -1]
   :ne [1 -1]
   :sw [-1 1]
   :se [1 1]})

(defn offset-coords
  "Offset the starting coordinate."
  [[x y] [dx dy]]
  [(+ x dx) (+ y dy)])


(defn dir-to-offset
  "Convert a direction to the offset."
  [dir]
  (directions dir))

(defn destination-coords
  [origin dir]
  (offset-coords origin (dir-to-offset dir)))

(defn neighbors [origin]
  (map offset-coords (vals directions) (repeat origin)))
