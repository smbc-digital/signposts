(ns visualise.ui.search.named-field-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [cljs-react-test.utils :as tu]
            [cljs-react-test.simulate :as sim]
            [visualise.common :refer [c ->render]]
            [dommy.core :as dommy :refer-macros [sel sel1]]
            [visualise.ui.search.named-field :refer [search-named-field current-value]]))

(use-fixtures :each (fn [test-fn]
                      (binding [c (tu/new-container!)]
                        (test-fn)
                        (tu/unmount! c))))

(deftest named-field-test

  (testing "test it renders field name from the ratom"
    (let [!ratom (atom {:text "Jim"})]
      (->render (search-named-field !ratom)))
    ; then assert whatever
    (is (= (dommy/value (sel1 [:input])) "Jim")))

  (testing "test that we can update the state of the ratom with the html component"
    (let [!ratom (atom {:text "Jim"})]
      (->render (search-named-field !ratom))
      ; then assert whatever
      (sim/change (sel1 [:input]) {:target {:value "Jimm"}})
      (is (= (current-value !ratom) "Jimm")))))
