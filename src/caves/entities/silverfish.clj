(ns caves.entities.silverfish
  (:require
   [caves.entities.core :refer [Entity get-id add-aspect]]
   [caves.entities.aspects.destructible :refer [Destructible]]
   [caves.entities.aspects.mobile :refer [Mobile move can-move?]]
   [caves.world :refer [get-entity-at]]
   [caves.coords :refer [neighbors]]))

(defrecord Silverfish [id glyph color location hp])

(defn make-silverfish [location]
  (map->Silverfish {:id (get-id)
                    :glyph "~"
                    :color :blue
                    :location location
                    :hp 1}))

(extend-type Silverfish Entity
  (tick [this world]
    (let [target (rand-nth (neighbors (:location this)))]
      (if (get-entity-at world target)
        world
        (move this target world)))))

(add-aspect Silverfish Mobile
            (can-move?
             [this dest world]
             (not (get-entity-at world dest))))

(add-aspect Silverfish Destructible)
