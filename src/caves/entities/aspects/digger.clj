(ns caves.entities.aspects.digger
  (:require [caves.entities.core :refer [defaspect]]
            [caves.world :refer [set-tile-floor check-tile]]))

(defaspect Digger
  (dig [this dest world]
    {:pre [(can-dig? this dest world)]}
    (set-tile-floor world dest))
  (can-dig? [this dest world]
    (check-tile world dest #{:wall})))
