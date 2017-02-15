(ns visualise.cards.faceted-search
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [cljsjs.flot]
            [cljsjs.flot.plugins.time]
            [cljs-time.core :as t]
            [cljs-time.periodic :as p]
            [visualise.ui.facet :refer [->cs facet-tree]])
  (:require-macros
    [devcards.core
     :as dc
     :refer [defcard defcard-doc defcard-rg deftest]]))

(def ratom (->cs {:facets [{:id    "GMP"
                            :name  "GMP"
                            :field :event-source
                            :count 3}
                           {:id    "SCHOOLS"
                            :name  "SCHOOLS"
                            :field :event-source
                            :count 5}
                           ]}
                 {"SCHOOLS" true}))

(defcard-rg simple-example
            [facet-tree ratom]
            ratom
            {:inspect-data true :history true}
            )