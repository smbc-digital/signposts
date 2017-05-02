(ns gov.stockport.sonar.visualise.ui.search.search-control-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [cljs-react-test.utils :as tu]
            [cljs-react-test.simulate :as sim]
            [gov.stockport.sonar.visualise.common :refer [c ->render]]
            [dommy.core :as dommy :refer-macros [sel sel1]]
            [stubadub.core :refer [calls-to] :refer-macros [with-stub]]
            [gov.stockport.sonar.visualise.query.client :as c]
            [gov.stockport.sonar.visualise.ui.search.search-control :refer [search-control]]))

(use-fixtures :each (fn [test-fn]
                      (binding [c (tu/new-container!)]
                        (test-fn)
                        (tu/unmount! c))))

(defn submit-button []
  (let [inputs (sel [:button])
        is-submit? #(dommy/has-class? % "search")]
    (first (filter is-submit? inputs))))

(deftest search-control-test

  (let [!state (atom {:text "Jim"})]

    (testing "it renders a search button"
      (->render (search-control !state (fn [])))
      (is (= (dommy/text (submit-button)) "Search")))))