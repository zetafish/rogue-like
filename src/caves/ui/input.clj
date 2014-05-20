(ns caves.ui.input
  (:require [caves.world :refer [random-world smooth-world]]
            [caves.ui.core :refer [->UI]]
            [caves.entities.player :refer [make-player]]
            [lanterna.screen :as s]))

(defn move [[x y] [dx dy]]
  [(+ x dx) (+ y dy)])

(defn reset-game [game]
  (let [fresh-world (random-world)]
    (-> game
        (assoc :world fresh-world)
        (assoc-in [:world :player] (make-player fresh-world))
        (assoc :uis [(->UI :play)]))))

(defmulti process-input (fn [game input]
                          (:kind (last (:uis game)))))

(defmethod process-input :play [game input]
  (case input
    :enter     (assoc game :uis [(->UI :win)])
    :backspace (assoc game :uis [(->UI :lose)])
    \q         (assoc game :uis [])
    \s (update-in game [:world] smooth-world)
    \h (update-in game [:location] move [-1 0])
    \j (update-in game [:location] move [0 1])
    \k (update-in game [:location] move [0 -1])
    \l (update-in game [:location] move [1 0])
    \H (update-in game [:location] move [-5 0])
    \J (update-in game [:location] move [0 5])
    \K (update-in game [:location] move [0 -5])
    \L (update-in game [:location] move [5 0])
    game
    ))

(defmethod process-input :start [game input]
  (reset-game game))

(defmethod process-input :win [game input]
  (assoc game :uis (if (= input :escape)
                     []
                     [(->UI :start)])))

(defmethod process-input :lose [game input]
  (assoc game :uis (if (= input :escape)
                     []
                     [(->UI :start)])))

(defn get-input [game screen]
  (assoc game :input (s/get-key-blocking screen)))
