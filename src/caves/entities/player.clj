(ns caves.entities.player
  (:require [caves.entities.core :refer :all]
            [caves.entities.aspects.mobile :refer [Mobile move can-move?]]
            [caves.entities.aspects.digger :refer [Digger dig can-dig?]]
            [caves.coords :refer [destination-coords]]
            [caves.world :refer [find-empty-tile get-tile-kind set-tile-floor]]))

(defrecord Player [id glyph location color])

(extend-type
    Player Entity
    (tick [this world]
      world))

(defn check-tile
  "Check that the tile at the destination passes the given predicate."
  [world dest pred]
  (pred (get-tile-kind world dest)))

(extend-type Player Mobile
  (move [this world dest]
    {:pre [(can-move? this world dest)]}
    (assoc-in world [:entities :player :location] dest))
  (can-move? [this world dest]
    (check-tile world dest #{:floor})))

(extend-type Player Digger
  (dig [this world dest]
    {:pre [(can-dig? this world dest)]}
    (set-tile-floor world dest))
  (can-dig? [this world dest]
    (check-tile world dest #{:wall})))

(defn make-player [location]
  (->Player :player "@" location :white))

(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        target (destination-coords (:location player) dir)]
    (cond
     (can-move? player world target) (move player world target)
     (can-dig? player world target) (dig player world target)
     :else world)))
