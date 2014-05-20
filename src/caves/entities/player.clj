(ns caves.entities.player
  (:require [caves.entities.core :refer :all]
            [caves.world :refer [find-empty-tile]]))

(defrecord Player [id glyph location])

(extend-type Player Entity
  (tick [this world]
    world))

(defn make-player [world]
  (->Player :player "@" (find-empty-tile world)))
