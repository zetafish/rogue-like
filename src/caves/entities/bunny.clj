(ns caves.entities.bunny
  (:require
   [caves.entities.core :refer [Entity get-id add-aspect]]
   [caves.entities.aspects.destructible :refer [Destructible]]
   [caves.entities.aspects.mobile :refer [Mobile move]]
   [caves.world :refer [find-empty-neighbor]]))

(defrecord Bunny [id glyph location color max-hp])

(add-aspect Bunny Destructible)
(add-aspect Bunny Mobile)

(extend-type Bunny Entity
  (tick [this world]
    (if-let [target (find-empty-neighbor world (:location this))]
      (move this target world)
      world)))

(defn make-bunny [location]
  (map->Bunny {:id (get-id)
               :glyph "v"
               :color :yellow
               :location location
               :hp 4
               :max-hp 4}))




