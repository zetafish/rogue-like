(ns caves.ui.drawing
  (:require [lanterna.screen :as s]
            [caves.util :refer [shear map2d enumerate]]))

(def screen-size [80 24])

(defn clear-screen [screen]
  (let [[cols rows] screen-size
        blank (apply str (repeat cols \space))]
    (doseq [row (range rows)]
      (s/put-string screen 0 row blank))))

(defn draw-entity [screen [start-x start-y] {:keys [location glyph color]}]
  (let [[entity-x entity-y] location
        x (- entity-x start-x)
        y (- entity-y start-y)]
    (s/put-string screen x y glyph {:fg color})
    (s/move-cursor screen x y)))

(defn highlight-player [screen [start-x start-y] player]
  (let [[player-x player-y] (:location player)
        x (- player-x start-x)
        y (- player-y start-y)]
    (s/move-cursor screen x y)))

(defn draw-world [screen vrows vcols [start-x start-y] end-x end-y tiles]
  (doseq [[vrow-idx mrow-idx] (map vector
                                     (range 0 vrows)
                                     (range start-y end-y))
            :let [row-tiles (subvec (tiles mrow-idx) start-x end-x)]]
      (doseq [vcol-idx (range vcols)
              :let [{:keys [glyph color]} (row-tiles vcol-idx)]]
        (s/put-string screen vcol-idx vrow-idx glyph {:fg color}))))

(defn draw-world [screen vrows vcols [ox oy] tiles]
  (letfn [(render-tile [tile]
            [(:glyph tile) {:fg (:color tile)}])]
    (let [tiles (shear tiles ox oy vcols vrows)
          sheet (map2d render-tile tiles)]
      (s/put-sheet screen 0 0 sheet))))

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

(defn draw-hud [screen game]
  (let [hud-row (dec (second (s/get-size screen)))
        player (get-in game [:world :entities :player])
        {:keys [location hp max-hp]} player
        [x y] location
        info (str "hp [" hp "/" max-hp "]")
        info (str info " loc: [" x "-" y "]")]
    (s/put-string screen 0 hud-row info)))

(defn draw-messages [screen messages]
  (doseq [[i msg] (enumerate messages)]
    (s/put-string screen 0 i msg {:fg :black :bg :white})))

(defmulti draw-ui
  (fn [ui game screen]
    (:kind ui)))

(defmethod draw-ui :play [ui game screen]
  (let [world (:world game)
        {:keys [tiles entities]} world
        player (:player entities)
        [cols rows] screen-size
        vcols cols
        vrows (dec rows)
        origin (get-viewport-coords game (:location player) vcols vrows)]
    (draw-world screen vrows vcols origin tiles)
    (doseq [entity (vals entities)]
      (draw-entity screen origin entity))
    (draw-hud screen game)
    (draw-messages screen (:messages player))
    (highlight-player screen origin player)))

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

