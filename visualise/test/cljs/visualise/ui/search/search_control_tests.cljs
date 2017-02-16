(ns visualise.ui.search.search-control-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [cljs-react-test.utils :as tu]
            [cljs-react-test.simulate :as sim]
            [visualise.common :refer [c ->render]]
            [dommy.core :as dommy :refer-macros [sel sel1]]
            [visualise.ui.search.search-control :refer [search-control]]))

(use-fixtures :each (fn [test-fn]
                      (binding [c (tu/new-container!)]
                        (test-fn)
                        (tu/unmount! c))))

(defn submit-button []
  (let [inputs (sel [:input])
        is-submit? #(= (dommy/attr % :type) "submit")]
    (first (filter is-submit? inputs))))

(deftest search-control-test

  (let [!state (atom {:text "Jim"})]

    (testing "it renders a search button"
      (->render (search-control !state))
      (is (= (dommy/value (submit-button)) "Search")))

    (testing "it includes a named field"
      (let [query-fn-args (atom nil)]
        (->render (search-control !state (fn [arg] (reset! query-fn-args arg))))
        (sim/click (submit-button) nil)
        (is (= @query-fn-args "Jim"))
        )
      ))

  )