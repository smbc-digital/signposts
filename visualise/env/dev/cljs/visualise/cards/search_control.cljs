(ns visualise.cards.search-control
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [visualise.ui.search.named-field :refer [search-named-field]]
            [visualise.ui.search.search-control :refer [search-control]])
  (:require-macros
    [devcards.core
     :as dc
     :refer [defcard defcard-doc defcard-rg deftest]]))

(defonce !state (atom { :text "Zeshan" }))

(defcard-rg search-control-wip
            [search-control !state]
            !state
            {:inspect-data true :history true})