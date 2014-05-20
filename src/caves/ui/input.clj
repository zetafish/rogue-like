(ns caves.ui.input
  (:require [caves.world :refer [random-world smooth-world]]
            [caves.ui.core :refer [->UI]]
            [caves.entities.player :refer [make-player move-player]]
            [lanterna.screen :as s]))

(defn move [[x y] [dx dy]]
  [(+ x dx) (+ y dy)])

(defn reset-game [game]
  (let [fresh-world (random-world)]
    (-> game
        (assoc :world fresh-world)
        (assoc-in [:world :entities :player] (make-player fresh-world))
        (assoc :uis [(->UI :play)]))))

(defmulti process-input (fn [game input]
                          (:kind (last (:uis game)))))

(defmethod process-input :play [game input]
  (case input
    :enter     (assoc game :uis [(->UI :win)])
    :backspace (assoc game :uis [(->UI :lose)])
    \q         (assoc game :uis [])
    \s (update-in game [:world] smooth-world)
    \h (update-in game [:world] move-player :w)
    \j (update-in game [:world] move-player :s)
    \k (update-in game [:world] move-player :n)
    \l (update-in game [:world] move-player :e)
    \H (update-in game [:world] move-player :nw)
    \J (update-in game [:world] move-player :ne)
    \K (update-in game [:world] move-player :sw)
    \L (update-in game [:world] move-player :se)
    game))

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
