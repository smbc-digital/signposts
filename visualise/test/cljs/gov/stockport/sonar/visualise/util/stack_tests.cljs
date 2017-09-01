(ns gov.stockport.sonar.visualise.util.stack-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.util.stack :as s]))

(deftest stacks

  (testing "are stateful"

    (let [{:keys [pop]} (s/new-stack [1 2 3])]
      (is (= (pop) 1))
      (is (= (pop) 2))
      (is (= (pop) 3))
      (is (= (pop) nil))
      (is (= (pop) nil))))

  (testing "and can be used like a stack (LIFO)"

    (let [{:keys [push pop]} (s/new-stack)]
      (is (= (pop) nil))
      (push 1)
      (push 2)
      (is (= (pop) 2))
      (push 3)
      (is (= (pop) 3))
      (is (= (pop) 1))
      (is (= (pop) nil))
      (is (= (pop) nil))))

  (testing "and can start empty"

    (let [{:keys [pop]} (s/new-stack [])]
      (is (= (pop) nil))))

  (testing "and can be given a default when popping"

    (let [{:keys [push pop]} (s/new-stack [] :value-when-empty :empty)]
      (is (= (pop) :empty))
      (push 1)
      (is (= (pop) 1))
      (is (= (pop) :empty))
      (is (= (pop) :empty))))

  (testing "and can tell us when it is empty"
    (let [{:keys [push pop is-empty?]} (s/new-stack [] :value-when-empty :empty)]
      (is (= (is-empty?) true))
      (is (= (pop) :empty))
      (is (= (is-empty?) true))
      (push 1)
      (is (= (is-empty?) false))
      (is (= (pop) 1))
      (is (= (is-empty?) true))
      (is (= (pop) :empty))
      (is (= (is-empty?) true)))))

(deftest colour-managers

  (testing "doesn't do much if there are no available colours"
    (let [{:keys [assign]} (s/new-colour-manager [])]
      (is (nil? (assign :some-pkey)))
      (is (nil? (assign :some-other-pkey)))))

  (testing "assigns to first available colour if there is one"
    (let [{:keys [assign]} (s/new-colour-manager [:a :b])]
      (is (= :a (assign :some-pkey)))
      (is (= :b (assign :some-other-pkey)))
      (is (nil? (assign :yet-another-pkey)))))

  (testing "provides lookup from the person to the colour"
    (let [{:keys [assign lookup]} (s/new-colour-manager [:a :b])]
      (assign :some-other-pkey)
      (assign :some-pkey)
      (is (= :b (lookup :some-pkey)))
      (is (= :a (lookup :some-other-pkey)))
      (is (nil? (lookup :some-unknown-pkey)))))

  (testing "allows for release of people"
    (let [{:keys [assign release]} (s/new-colour-manager [:a])]
      (is (= :a (assign :some-pkey)))
      (is (nil? (assign :some-other-pkey)))
      (release :some-pkey)
      (is (= :a (assign :some-other-pkey)))))

  (testing "indicates availability of colours"
    (let [{:keys [assign release available?]} (s/new-colour-manager [:a :b])]
      (is (available?))
      (assign :some-pkey)
      (is (available?))
      (assign :another-pkey)
      (is (not (available?)))
      (release :some-pkey)
      (is (available?)))))
