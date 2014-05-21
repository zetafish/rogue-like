(ns caves.entities.lichen
  (:require [caves.entities.core :refer [Entity get-id]]
            [caves.entities.aspects.destructible :refer [Destructible]]))

(defrecord Lichen [id glyph location color hp])

(defn make-lichen [location]
  (->Lichen (get-id) "F" location :green 1))

(extend-type
    Lichen Entity
    (tick [this world]
      world))

(extend-type
    Lichen Destructible
    (take-damage [{:keys [id] :as this} world damage]
      (let [damaged-this (update-in this [:hp] - damage)]
        (if-not (pos? (:hp damaged-this))
          (update-in world [:entities] dissoc id)
          (update-in world [:entities id] assoc damaged-this)))))
