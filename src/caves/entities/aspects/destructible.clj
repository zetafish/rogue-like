(ns caves.entities.aspects.destructible
  (:require [caves.entities.core :refer [defaspect]]
            [caves.entities.aspects.receiver :refer [send-message-nearby]]))

(defaspect Destructible
  (take-damage  [{:keys [id] :as this} damage world]
               (let [damaged-this (update-in this [:hp] - damage)]
                 (if-not (pos? (:hp damaged-this))
                   (let [world (update-in world [:entities] dissoc id)
                         world (send-message-nearby
                                (:location this)
                                (format "The %s dies." (:name this))
                                world)]
                     world)
                   (assoc-in world [:entities id] damaged-this))))
  (defense-value [this world]
    (get this :defence 0)))
