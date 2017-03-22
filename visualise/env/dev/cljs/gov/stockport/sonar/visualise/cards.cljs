(ns gov.stockport.sonar.visualise.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [gov.stockport.sonar.visualise.cards.flot]
            [gov.stockport.sonar.visualise.cards.faceted-search]
            [gov.stockport.sonar.visualise.cards.search-control]
            [gov.stockport.sonar.visualise.cards.zz-richard])
  (:require-macros
    [devcards.core]))

(reagent/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
