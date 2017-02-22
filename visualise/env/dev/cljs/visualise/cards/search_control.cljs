(ns visualise.cards.search-control
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [visualise.ui.search.search-control :as sc])
  (:require-macros
    [devcards.core
     :as dc
     :refer [defcard defcard-doc defcard-rg deftest]]))

(defonce !state (atom {}))

(defcard-rg search-control-ii-wip
            [sc/search-control !state]
            !state
            {:inspect-data true :history true})
