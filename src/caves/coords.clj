(ns caves.coords)

(defn offset-coords [[x y] [dx dy]]
  [(+ x dx) (+ y dy)])

(defn dir-to-offset [dir]
  (case dir
    :w [-1 0]
    :e [1 0]
    :n [0 -1]
    :s [0 1]
    :nw [-1 -1]
    :ne [1 -1]
    :sw [-1 1]
    :se [1 1]))

(defn destination-coords [origin dir]
  (offset-coords origin (dir-to-offset dir)))
