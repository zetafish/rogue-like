(ns caves.util)

(defn abs [x]
  (Math/abs x))

(defn map2d
  "Map a function across a 2d sequence"
  [f s]
  (map (partial map f) s))

(defn slice
  "Slice a sequence."
  [s start width]
  (->> s
       (drop start)
       (take width)))

(defn shear
  "Shear a 2d sequence, returning a smaller one."
  [s x y w h]
  (map #(slice % x w)
       (slice s y h)))

(defn enumerate [s]
  (map vector (iterate inc 0) s))
