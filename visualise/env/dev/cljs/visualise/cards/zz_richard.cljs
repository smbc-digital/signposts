(ns visualise.cards.zz-richard
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
  ###Richard's Scratchpad
  ")

(defn path [id]
  [:search-control :query-fields (keyword (str "subcomp-" id))])

(defn subcomp [!state id]
  ^{:getter (fn [] (* 2 id))}
  [:div
   [:label (str id " ")
    [:input
     {:type      :text
      :value     (:val (get-in @!state (path id)))
      :on-change #(swap! !state assoc-in (path id) {:val (-> % .-target .-value)})}]]])

(defn subcomps [!state]
  (doall (map #(subcomp !state %) (range 1 (+ 1 (:comps @!state))))))

(defonce !state (atom {:comps 5}))

(defn do-something-with [subcomps]
  (doall (map (fn [subcomp] (println ((:getter (meta subcomp))))) subcomps)))

(defn controller [!state]
  (let [subcomps (subcomps !state)]
    `[:div
      ~@subcomps
      ~[:input {:type     :submit
                :value    "PANIC!!!"
                :on-click (fn [] (do-something-with subcomps))}]]))

(defcard-rg create-components-programmatically
            [controller !state]
            !state
            {:inspect-data true :history true})
