(ns visualise.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [visualise.cards.timelines]
            [visualise.cards.flot]
            [visualise.cards.explore]
            [visualise.cards.search-named-field]
            [visualise.cards.faceted-search]
            [visualise.cards.search-control]
            [visualise.cards.search-control-ii]
            [visualise.cards.zz-richard])
  (:require-macros
    [devcards.core]))

(reagent/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
