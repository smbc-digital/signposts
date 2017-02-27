(ns visualise.common.ui.colors)

; these colors correspond to colors defined in SASS at
; sonar/visualise/src/sass/partials/_colors.scss

(def color-priority [:red :yellow :green :aqua :blue :purple])

(defn color [idx] (or (get color-priority idx) :black))

(def color-map
  {:red    "#f36624"
   :yellow "#ffc502"
   :green  "#8dc702"
   :aqua   "#01a79d"
   :blue   "#2e3292"
   :purple "#91278e"
   :black  "#000000"})
