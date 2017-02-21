(ns visualise.cards.search-control-ii
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [visualise.ui.search.search-control-ii :as sc])
  (:require-macros
    [devcards.core
     :as dc
     :refer [defcard defcard-doc defcard-rg deftest]]))

(defonce !state (atom {}))

(defcard-rg search-control-ii-wip
            [sc/search-control-ii !state]
            !state
            {:inspect-data true :history true})
