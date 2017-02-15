(ns visualise.ui.facet-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [cljs-react-test.utils :as tu]
            [cljs-react-test.simulate :as sim]
            [visualise.common :refer [c ->render]]
            [reagent.core :refer [render]]
            [dommy.core :as dommy :refer-macros [sel sel1]]
            [visualise.ui.facet :refer [->cs facet-tree]]))

(use-fixtures :each (fn [test-fn]
                      (binding [c (tu/new-container!)]
                        (test-fn)
                        (tu/unmount! c))))

; this is effectively a page object
(defn facet-info
  ([] (map #(facet-info %) (sel [:label])))
  ([elem] (let [cbelem (sel1 elem [:input])
                id (dommy/value cbelem)
                label (dommy/text elem)
                checked (.-checked cbelem)]
            [id label checked])))

; this simulates the react change event on the facet with the specified id
(defn select [id]
  (sim/change
    (first (->> (sel [:input]) (filter #(= (dommy/value %) id))))
    nil))


(deftest facet-test

  (testing "with a single level of facets"

    (def simple-facet-data {:facets [{:id    "GMP"
                                      :name  "GMP"
                                      :field :event-source
                                      :count 1}
                                     {:id    "SCHOOLS"
                                      :name  "SCHOOLS"
                                      :field :event-source
                                      :count 3}]})

    (testing "displays the basic data with nothing selected"

      (->render (facet-tree (->cs simple-facet-data)))
      (is (= (facet-info) [["GMP" "GMP (1)" false]
                           ["SCHOOLS" "SCHOOLS (3)" false]])))

    (testing "uses state to show selected items"

      (->render (facet-tree (->cs simple-facet-data {"SCHOOLS" true})))
      (is (= (facet-info) [["GMP" "GMP (1)" false]
                           ["SCHOOLS" "SCHOOLS (3)" true]])))

    (testing "state changes on facet selection"

      (let [!cs (->cs simple-facet-data {})]
        (->render (facet-tree !cs))
        (is (= (get-in @!cs [:facet-state "GMP"]) nil))
        (is (= (get-in @!cs [:facet-state "SCHOOLS"]) nil))
        (select "GMP")
        (is (= (get-in @!cs [:facet-state "GMP"]) true))
        (is (= (get-in @!cs [:facet-state "SCHOOLS"]) nil))
        (select "SCHOOLS")
        (is (= (get-in @!cs [:facet-state "GMP"]) true))
        (is (= (get-in @!cs [:facet-state "SCHOOLS"]) true))
        (select "GMP")
        (is (= (get-in @!cs [:facet-state "GMP"]) false))
        (is (= (get-in @!cs [:facet-state "SCHOOLS"]) true))))
    ))


; for a more complicated version of the control...
(def nested-facet-data
  {:control-state {}
   :data          {:facets [{:id     "GMP"
                             :name   "GMP"
                             :field  :event-source
                             :count  1
                             :facets [{:id    "GMP.ASBO"
                                       :name  "ASBO"
                                       :field :event-type
                                       :count 1}]}
                            {:id     "HOMES"
                             :name   "HOMES"
                             :count  3
                             :facets [{:id    "HOMES.ARREARS"
                                       :name  "ARREARS"
                                       :field :event-type
                                       :count 1}
                                      {:id    "HOMES.EVICTION"
                                       :name  "EVICTION"
                                       :field :event-type
                                       :count 2}]}
                            {:id     "SCHOOLS"
                             :name   "SCHOOLS"
                             :count  5
                             :field  :event-type
                             :facets [{:id    "SCHOOLS.AWOL"
                                       :name  "AWOL"
                                       :field :event-type
                                       :count 2}
                                      {:id    "SCHOOLS.EXCLUSION"
                                       :name  "EXCLUSION"
                                       :field :event-type
                                       :count 3}]}]}})
