(ns gov.stockport.sonar.visualise.data.colours)

; these colors correspond to colors defined in SASS at
; sonar/visualise/src/sass/partials/_colors.scss

(def colour-priority
  [:orange :yellow :green :turquoise :navy :purple
   :pink :forest-green :burgundy :lighter-purple])

(defn colour [idx] (or (get colour-priority idx) :black))

(defn colour-for [items]
  "Gets colour form colour map it is in the list else black"
  (if (> (count items) (count colour-priority))
    (fn [_] :black)
    (fn [idx] (colour idx))))

(def colour-map
  {
   :yellow "#ffc502"
   :green  "#8dc702"
   :orange "#f36624"
   :turquoise "#01a79d"
   :purple "#91278e"
   :navy   "#2e3292"
   :pink "#da4167"
   :forest-green "#013326"
   :lighter-purple "#ca9de2"
   :burgundy "#540e2b"
   :black  "rgba(0,0,0,0.4)"})
