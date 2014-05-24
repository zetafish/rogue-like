(ns caves.entities.player
  (:require
   [caves.entities.core :refer :all]
   [caves.entities.core :refer [add-aspect]]
   [caves.entities.aspects.mobile :refer [Mobile move can-move?]]
   [caves.entities.aspects.digger :refer [Digger dig can-dig?]]
   [caves.entities.aspects.attacker :refer [Attacker attack]]
   [caves.entities.aspects.destructible :refer [Destructible take-damage]]
   [caves.coords :refer [destination-coords]]
   [caves.world :refer [find-empty-tile get-tile-kind set-tile-floor
                        get-entity-at is-empty? check-tile]]))

(defrecord Player [id glyph location color]) 

(add-aspect Player Mobile)
(add-aspect Player Digger)


(extend-type Player Entity
             (tick [this world]
               world))

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
