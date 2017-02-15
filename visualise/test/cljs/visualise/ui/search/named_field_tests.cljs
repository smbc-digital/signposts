(ns visualise.ui.search.named-field-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [cljs-react-test.utils :as tu]
            [reagent.core :refer [render]]
            [dommy.core :as dommy :refer-macros [sel sel1]]
            [visualise.ui.search.named-field :refer [search-name]]))

(def ^:dynamic c)

(use-fixtures :each (fn [test-fn]
                      (binding [c (tu/new-container!)]
                        (test-fn)
                        (tu/unmount! c))))

(deftest named-field-test
  (testing "test we can render a search control"
    ; given our definition of a search control
    (let [subject (search-name)
          ; when it is rendered
          _ (render subject c)
          ; log out the structure for convenience
          ; _ (println "** " (dommy/attr (sel1 [:input]) :type))
          ]
      ; then assert whatever
      (is (= (dommy/attr (sel1 [:input]) :type) "text")))))


