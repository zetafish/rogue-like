(ns caves.ui.drawing
  (:require [lanterna.screen :as s]))

(def screen-size [80 24])

(defn clear-screen [screen]
  (let [[cols rows] screen-size
        blank (apply str (repeat cols \space))]
    (doseq [row (range rows)]
      (s/put-string screen 0 row blank))))

(defn draw-player [screen start-x start-y player]
  (let [[player-x player-y] (:location player)
        x (- player-x start-x)
        y (- player-y start-y)]
    (s/put-string screen x y (:glyph player) {:fg :white})
    (s/move-cursor screen x y)))

(defn draw-world [screen vrows vcols start-x start-y end-x end-y tiles]
  (doseq [[vrow-idx mrow-idx] (map vector
                                     (range 0 vrows)
                                     (range start-y end-y))
            :let [row-tiles (subvec (tiles mrow-idx) start-x end-x)]]
      (doseq [vcol-idx (range vcols)
              :let [{:keys [glyph color]} (row-tiles vcol-idx)]]
        (s/put-string screen vcol-idx vrow-idx glyph {:fg color}))))

(defn get-viewport-coords [game [player-x player-y] vcols vrows]
  (let [location (:location game)
        [center-x center-y] location

        tiles (:tiles (:world game))

        map-rows (count tiles)
        map-cols (count (first tiles))

        start-x (max 0 (- player-x (int (/ vcols 2))))
        start-y (max 0 (- player-y (int (/ vrows 2))))

        end-x (+ start-x vcols)
        end-x (min end-x map-cols)

        end-y (+ start-y vrows)
        end-y (min end-y map-rows)

        start-x (- end-x vcols)
        start-y (- end-y vrows)]
    [start-x start-y end-x end-y]))

(defn draw-hud [screen game start-x start-y]
  (let [hud-row (dec (second screen-size))
        [x y] (get-in game [:world :entities :player :location])
        info (str "loc: [" x "-" y "]")
        info (str info " start: [" start-x "-" start-y "]")]
    (s/put-string screen 0 hud-row info)))

(defmulti draw-ui
  (fn [ui game screen]
    (:kind ui)))

(defmethod draw-ui :play [ui game screen]
  (let [[cols rows] screen-size
        vcols cols
        vrows (dec rows)
        player (get-in game [:world :entities :player])
        tiles (get-in game [:world :tiles])
        [start-x start-y end-x end-y] (get-viewport-coords game (:location player) vcols vrows)]
    (draw-world screen vrows vcols start-x start-y end-x end-y tiles)
    (draw-player screen start-x start-y player)
    (draw-hud screen game start-x start-y)))

(defmethod draw-ui :start [ui game screen]
  (s/put-string screen 0 0 "Welcome to the caves of Clojure")
  (s/put-string screen 0 1 "Press enter to win."))

(defmethod draw-ui :win [ui game screen]
  (s/put-string screen 0 0 "Concratulations, you won!")
  (s/put-string screen 0 1 "Press escape to exit, other to restart"))

(defmethod draw-ui :lose [ui game screen]
  (s/put-string screen 0 0 "Sorry, you lost")
  (s/put-string screen 0 1 "Press escape to exit, other to restart"))

(defn draw-game [game screen]
  (clear-screen screen)
  (doseq [ui (:uis game)]
    (draw-ui ui game screen))
  (s/redraw screen))

