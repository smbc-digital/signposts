(ns visualise.cards.timelines
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [visualise.core :as core]
            [visualise.ui.explore :as explore]
            [devcards.core :as dc]
            [cljs-time.core :as t])
  (:require-macros
    [devcards.core
     :as dc
     :refer [defcard defcard-doc defcard-rg deftest]]))

(defcard-doc
  "
  ##Timelines

  The timeline component take a list of _events_ and places them in a graph.

  ```
  ; an event is just a map with 3 required fields, and an optional duration

  {:timestamp     <some-date-time>
   :event-source  \"SOURCE\"
   :event-type    \"TYPE\"
   :duration      <optional-length-in-days>
   :...           <other-keys>
  }
  ```
  ")

(def example-events
  [{:timestamp (t/now) :event-source "SOURCE" :event-type "TYPE"}])

(def two-events
  [{:timestamp (t/now) :event-source "SOURCE" :event-type "TYPE"}
   {:timestamp (t/plus (t/now) (t/months 22)) :event-source "SOURCE" :event-type "TYPE"}])

(defcard-rg timeline-with-single-event
            [explore/graph (explore/view-state {:vb-h 75}) example-events]
            example-events)

(defcard-rg timeline-with-two-events
            [explore/graph (explore/view-state {:vb-h 75}) two-events]
            two-events)
