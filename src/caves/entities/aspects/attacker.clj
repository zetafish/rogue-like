(ns caves.entities.aspects.attacker
  (:require [caves.entities.aspects.destructible :refer [defense-value
                                                         take-damage]]))

(defprotocol Attacker
  (attack [this target world]
    {:pre [(satisfies? Destructible target)]}
    (let [damage (get-damage this target world)]
      (take-damage target damage)))
  (attack-value [this world]
    (get this :attack 1)))

(defn get-damage [attacker target world]
  (let [attack (attack-value attacker world)
        defense (defense-value target world)
        max-damage (max 0 (- attack defense))
        damage (inc (rand-int max-damage))]
    damage))
