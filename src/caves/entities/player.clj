(ns caves.entities.player
  (:require [caves.entities.core :refer :all]
            [caves.entities.aspects.mobile :refer [Mobile move can-move?]]
            [caves.entities.aspects.digger :refer [Digger dig can-dig?]]
            [caves.coords :refer [destination-coords]]
            [caves.world :refer [find-empty-tile get-tile-kind set-tile-floor]]))

(defrecord Player [id glyph location])

(extend-type Player Entity
  (tick [this world]
    world))

(defn check-tile
  "Check that the tile at the destination passes the given predicate."
  [world dest pred]
  (pred (get-tile-kind world dest)))

(extend-type Player Mobile
  (move [this world dest]
    {:pre (can-move? this world dest)}
    (assoc-in world [:player :location] dest))
  (can-move? [this world dest]
    (check-tile world dest #{:floor})))

(extend-type Player Mobile
  (move [this world dest]
    {:pre [(can-move? this world dest)]}
    (assoc-in world [:player :location] dest))
  (can-move? [this world dest]
    (check-tile world dest #{:floor})))

(defn make-player [world]
  (->Player :player "@" (find-empty-tile world)))

(defn move-player [world dir]
  (let [player (:player world)
        target (destination-coords (:location player) dir)]
    (cond
      (can-move? player world target) (move player world target)
      :else world)))
