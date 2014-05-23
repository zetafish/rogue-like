(ns caves.core
  (:require [caves.ui.core :refer [->UI]]
            [caves.ui.drawing :refer [draw-game]]
            [caves.ui.input :refer [get-input process-input]]
            [caves.entities.core :refer [tick]]
            [lanterna.screen :as s]))

(defrecord World [])

(defrecord Game [world uis input])

(defn tick-entity [world entity]
  (tick entity world))

(defn tick-all [world]
  (reduce tick-entity world (vals (:entities world))))

(defn run-game [game screen]
  (try
    (loop [{:keys [input uis] :as game} game]
      (when-not (empty? uis))
      (draw-game game screen)
      (if (nil? input)
        (recur (get-input (update-in game [:world] tick-all) screen))
        (recur (process-input (dissoc game :input) input))))
    (catch Exception e
      (.printStackTrace e))))

(defn new-game []
  (assoc (->Game nil [(->UI :start)] nil)
    :location [40 20]))

(defn main
  ([screen-type] (main screen-type false))
  ([screen-type block?]
     (letfn [(go []
                 (let [screen (s/get-screen screen-type)]
                   (s/in-screen screen
                                (run-game (new-game) screen))))]
       (if block?
         (go)
         (future (go))))))

(defn -main [& args]
  (let [args (set args)
        screen-type (cond
                    (args ":swing") :swing
                    (args ":text") :text
                    :else :auto)]
    (main screen-type true)))

(defn -main []
  (main :swing true))

