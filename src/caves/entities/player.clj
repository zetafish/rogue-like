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
             (move [this world dest]
               {:pre [(can-move? this world dest)]}
               (assoc-in world [:entities :player :location] dest))
             (can-move? [this world dest]
               (is-empty? world dest)))

(extend-type Player Digger
             (dig [this world dest]
               {:pre [(can-dig? this world dest)]}
               (set-tile-floor world dest))
             (can-dig? [this world dest]
               (check-tile world dest #{:wall})))

(extend-type Player Attacker
             (attack [this world target]
               {:pre [(satisfies? Destructible target)]}
               (let [damage 1]
                 (take-damage target world damage))))

(defn make-player [location]
  (->Player :player "@" location :white))

(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        target (destination-coords (:location player) dir)
        entity-at-target (get-entity-at world target)]
    (cond
     entity-at-target (attack player world entity-at-target)
     (can-move? player world target) (move player world target)
     (can-dig? player world target) (dig player world target)
     :else world)))
