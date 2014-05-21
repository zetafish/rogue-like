(ns caves.entities.lichen
  (:require [caves.entities.core :refer [Entity get-id]]))

(defrecord Lichen [id glyph location color])

(defn make-lichen [location]
  (->Lichen (get-id) "F" location :red))

(extend-type Lichen Entity
             (tick [this world]
               world))

