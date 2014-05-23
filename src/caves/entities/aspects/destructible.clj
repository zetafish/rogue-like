(ns caves.entities.aspects.destructible
  (:require [caves.entities.core :refer [defaspect]]))

(defaspect Destructible
  (take-damage [{:keys [id] :as this} damage world]
               (let [damaged-this (update-in this [:hp] - damage)]
                 (if-not (pos? (:hp damaged-this))
                   (update-in world [:entities] dissoc id)
                   (assoc-in world [:entities id] damaged-this)))))

(defprotocol Destructible
  (take-damage [this damage world]
    "Take the given amount of damage and update the world"))
