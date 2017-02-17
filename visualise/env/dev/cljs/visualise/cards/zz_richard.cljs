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
  ###Dynamic Sub-Components

  subcomp is one one of our traditional reagent components that produces an input box with an id of some description

  ```
  (defn subcomp [!state id]
    (let [getter (fn [] (:val (get-in @!state (path id))))]
      ; this ^{} attaches a metadata map to the hiccup list which can be
      ; accessed programmatically if you have a reference to the list
      ; in this case we are providing a 'getter' that can return the data
      ; from the input field
      ^{:getter getter}
      [:div
       [:label (str id \" \")
        [:input
         {:type      :text
          :value     (getter)
          :on-change #(swap! !state assoc-in (path id) {:val (-> % .-target .-value)})}]]]))
  ```

  subcomps is just a function that maps over subcomp and builds n (defined in !state) of the input subcomp components

  the controller renders a div containing the list produced by subcomps

  ```
  (defn controller [!state]
    (let [subcomps (subcomps !state)]
      `[:div
        ~@subcomps
        ~[:input {:type     :submit
                  :value    \"PANIC!!!\"
                  :on-click (fn [] (do-something-with subcomps))}]]))
  ```

  the syntax quoting is the interesting bit here...

  ```
  ; essentially the syntax ` and ~@ is needed to allow the controller
  ; to return a [:div [:div] [:div] ... ]
  ; rather than [:div [ [:div] [:div] ... ] ] that you would get without it
  ; it's subtle ask Zeshan for more info :-)
  ```
  ")

(defonce !state (atom {:comps 5}))

(defn path [id]
  [:search-control :query-fields (keyword (str "subcomp-" id))])

(defn subcomp [!state id]
  (let [getter (fn [] (:val (get-in @!state (path id))))]
    ^{:getter getter}
    [:div
     [:label (str id " ")
      [:input
       {:type      :text
        :value     (getter)
        :on-change #(swap! !state assoc-in (path id) {:val (-> % .-target .-value)})}]]]))

(defn subcomps [!state]
  (doall (map #(subcomp !state %) (range 1 (+ 1 (:comps @!state))))))

(defn do-something-with [subcomps]
  (doall (map (fn [subcomp] (println ((:getter (meta subcomp))))) subcomps)))

(defn controller [!state]
  (let [subcomps (subcomps !state)]
    `[:div
      ~[:label "number of sub-components " [:input {:type  :text
                                                   :value (:comps @!state)
                                                    :on-change #(swap! !state assoc :comps (-> % .-target .-value int))}]]
      ~@subcomps
      ~[:input {:type     :submit
                :value    "PANIC!!!"
                :on-click (fn [] (do-something-with subcomps))}]]))

(defcard-rg create-components-programmatically
            [controller !state]
            !state
            {:inspect-data true :history true})
