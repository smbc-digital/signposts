(ns visualise.cards.search-named-field
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [visualise.ui.search.named-field :refer [search-named-field]])
  (:require-macros
    [devcards.core
     :as dc
     :refer [defcard defcard-doc defcard-rg deftest]]))

(def !state (atom { :text "Jim" }))

(defcard-rg simple-search-named-field
            [search-named-field !state]
            !state
            {:inspect-data true :history true})