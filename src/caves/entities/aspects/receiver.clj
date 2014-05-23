(ns caves.entities.aspects.receiver
  (:require [caves.entities.core :refer [defaspect]]
            [caves.world :refer [get-entities-around]]))

(defaspect Receiver
  (receive-message [this message world]
    (update-in world [:entities (:id this) :messages] conj messages)))

(defn send-message [entity message args world]
  (if (satisfies? Receiver entity)
    (receive-message entity (apply format message args) world)
    world))
