(ns visualise.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [visualise.cards.timelines])
  (:require-macros
    [devcards.core]))

(reagent/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
