(ns visualise.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [visualise.cards.timelines]
            [visualise.cards.flot])
  (:require-macros
    [devcards.core]))

(reagent/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
