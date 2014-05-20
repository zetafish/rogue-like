(ns caves.entities.aspects.digger)

(defprotocol Digger
  (dig [this world target]
    "Dig a location".)
  (can-dig? [this world target]
    "Return whether the entuty can dig the new location."))
