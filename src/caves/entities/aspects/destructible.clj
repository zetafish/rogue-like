(ns caves.entities.aspects.destructible)

(defprotocol Destructible
  (take-damage [this damage world]
    "Take the given amount of damage and update the world"))
