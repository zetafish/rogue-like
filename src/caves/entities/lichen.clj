(ns caves.entities.lichen
  (:require [caves.entities.core :refer [Entity get-id add-aspect]]
            [caves.entities.aspects.destructible :refer [Destructible]]
            [caves.world :refer [find-empty-neighbor]]))

(defrecord Lichen [id glyph location color hp])

(add-aspect Lichen Destructible)

(defn make-lichen [location]
  (->Lichen (get-id) "F" location :green 1))

(defn should-grow? []
  (< (rand) 0.01))

(defn grow [lichen world]
  (if-let [target (find-empty-neighbor world (:location lichen))]
    (let [new-lichen (make-lichen target)]
      (assoc-in world [:entities (:id new-lichen)] new-lichen))
    world))

(extend-type Lichen Entity
             (tick [this world]
               (if (should-grow?)
                 (grow this world)
                 world)))

