(ns caves.entities.player
  (:require [caves.entities.core :refer :all]
            [caves.entities.aspects.mobile :refer [Mobile move can-move?]]
            [caves.entities.aspects.digger :refer [Digger dig can-dig?]]
            [caves.entities.aspects.attacker :refer [Attacker attack]]
            [caves.entities.aspects.destructible :refer [Destructible take-damage]]
            [caves.coords :refer [destination-coords]]
            [caves.world :refer [find-empty-tile get-tile-kind set-tile-floor
                                 get-entity-at is-empty?]]))

(defrecord Player [id glyph location color])

(defn check-tile
  "Check that the tile at the destination passes the given predicate."
  [world dest pred]
  (pred (get-tile-kind world dest)))

(extend-type Player Entity
             (tick [this world]
               world))

(extend-type Player Mobile
             (move [this dest world]
               {:pre [(can-move? this dest world)]}
               (assoc-in world [:entities :player :location] dest))
             (can-move? [this dest world]
               (is-empty? world dest)))

(extend-type Player Digger
             (dig [this dest world]
               {:pre [(can-dig? this dest world)]}
               (set-tile-floor world dest))
             (can-dig? [this dest world]
               (check-tile world dest #{:wall})))

(extend-type Player Attacker
             (attack [this target world]
               {:pre [(satisfies? Destructible target)]}
               (let [damage 1]
                 (take-damage target damage world))))

(defn make-player [location]
  (->Player :player "@" location :white))

(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        target (destination-coords (:location player) dir)
        entity-at-target (get-entity-at world target)]
    (cond
     entity-at-target (attack player entity-at-target world)
     (can-move? player target world) (move player target world)
     (can-dig? player target world) (dig player target world)
     :else world)))
