(ns visualise.ui.search.named-field-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [cljs-react-test.utils :as tu]
            [visualise.common :refer [c ->render]]
            [dommy.core :as dommy :refer-macros [sel sel1]]
            [visualise.ui.search.named-field :refer [search-named-field]]))

(use-fixtures :each (fn [test-fn]
                      (binding [c (tu/new-container!)]
                        (test-fn)
                        (tu/unmount! c))))

(deftest named-field-test

  (testing "test we can render a search control"
    ; given our definition of a search control
    (let [subject (search-named-field)
          ; when it is rendered
          _ (->render subject)
          ; log out the structure for convenience
          ; _ (println "** " (dommy/attr (sel1 [:input]) :type))
          ]
      ; then assert whatever
      (is (= (dommy/attr (sel1 [:input]) :type) "text"))))

  ;(testing "typing into the named field modifies the component state"
  ;  (->render (search-named-field)))

  )


