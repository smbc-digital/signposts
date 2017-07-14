(ns gov.stockport.sonar.visualise.ui.results.individual-cards-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.ui.results.individual-cards :as ic]))

(deftest individual-cards-test
  (testing "Scroll function is called when we select a new event for the first time"
    (reset! ic/!current nil)
    (let [was-called (atom false)
          !data (atom {:selected-event "something"})
          subject-wrapped-scroll (ic/wrap-scroll !data (fn [] (reset! was-called true)))]
      (subject-wrapped-scroll)
      (is (= true @was-called))))

  (testing "Scroll function is not called again if selected event doesn't change"
    (reset! ic/!current nil)
    (let [invocation-count (atom 0)
          !data (atom {:selected-event "something"})
          subject-wrapped-scroll (ic/wrap-scroll !data (fn [] (swap! invocation-count inc)))]
      (subject-wrapped-scroll)
      (is (= 1 @invocation-count))
      (subject-wrapped-scroll)
      (is (= 1 @invocation-count))))

  (testing "Scroll function is called again when the selected event changes"
    (reset! ic/!current nil)
    (let [invocation-count (atom 0)
          !data (atom {:selected-event "something"})
          subject-wrapped-scroll (ic/wrap-scroll !data (fn [] (swap! invocation-count inc)))]
      (subject-wrapped-scroll)
      (is (= 1 @invocation-count))
      (swap! !data assoc :selected-event "something else")
      (subject-wrapped-scroll)
      (is (= 2 @invocation-count))
      ))

  (testing "Deselect and Select should still Scroll"
    (reset! ic/!current nil)
    (let [invocation-count (atom 0)
          !data (atom {:selected-event "something"})
          subject-wrapped-scroll (ic/wrap-scroll !data (fn [] (swap! invocation-count inc)))]
      (subject-wrapped-scroll)
      (is (= 1 @invocation-count))
      (swap! !data dissoc :selected-event)
      (subject-wrapped-scroll)
      (is (= 1 @invocation-count))
      (swap! !data assoc :selected-event "something")
      (subject-wrapped-scroll)
      (is (= 2 @invocation-count))
      ))

  )