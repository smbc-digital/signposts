(ns visualise.cards.search-named-field
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [visualise.ui.search.named-field :refer [search-named-field]])
  (:require-macros
    [devcards.core
     :as dc
     :refer [defcard defcard-doc defcard-rg deftest]]))

(defcard-rg simple-search-named-field
            [search-named-field]
            {:inspect-data true :history true}
            )