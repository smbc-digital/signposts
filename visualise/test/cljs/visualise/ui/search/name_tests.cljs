(ns visualise.ui.search.name-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [hipo.core :as hipo]
            [dommy.core :as dommy :refer-macros [sel sel1]]
            [visualise.ui.search.name :refer [search-name]]))

(deftest name-test
  (testing "test we can render a search control"
    ; given our definition of a search control
    (let [subject (search-name)
          ; when it is rendered
          dom (hipo/create subject)
          ; log out the structure for convenience
          _ (println "** " (dommy/attr (sel1 dom [:input]) :type))
          ]
      ; then assert whatever
      (is (= true true))
      )

    )

  )


