(ns gov.stockport.sonar.visualise.data.colours)

; these colors correspond to colors defined in SASS at
; sonar/visualise/src/sass/partials/_colors.scss

(def colour-priority [:red :yellow :green :aqua :blue :purple])

(defn colour [idx] (or (get colour-priority idx) :black))

(defn colour-for [items]
  (if (> (count items) (count colour-priority))
    (fn [_] :black)
    (fn [idx] (colour idx))))

(def colour-map
  {:red    "#f36624"
   :yellow "#ffc502"
   :green  "#8dc702"
   :aqua   "#01a79d"
   :blue   "#2e3292"
   :purple "#91278e"
   :black  "rgba(0,0,0,0.4)"})
