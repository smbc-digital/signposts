(ns gov.stockport.sonar.visualise.ui.results.signposting-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.ui.results.signposting :as s]))

(deftest signposting-tests

  (with-redefs
    [s/signposts {:default {:default {:fields [{:name    "Default 1"
                                                :default "(some default 1)"
                                                :source  :source-field}
                                               {:name    "Default 2"
                                                :default "(some default 2)"}]}}
                  :GMP     {:default {:fields [{:name    "Incident"
                                                :default "(type of incident)"}]}
                            :ASBO    {:fields [{:name    "Asbo Field"
                                                :default "(type of asbo)"}]}}}]

    (testing "when event source and type have not been configured"

      (let [{:keys [fields]} (s/signpost-for {:event-source :not-configured :event-type :not-configured})]

        (testing "provides named fields"
          (is (= (map :name fields) ["Default 1" "Default 2"])))

        (testing "provides default values"
          (is (= (map :value fields) ["(some default 1)" "(some default 2)"]))))

      (testing "can source data from the underlying event when present"
        (is (= (map :value (:fields (s/signpost-for {:source-field "some event data"})))
               ["some event data" "(some default 2)"]))))

    (testing "provides default fields for a configured event source"
      (let [{:keys [fields]} (s/signpost-for {:event-source :GMP :event-type :not-configured})]
        (is (= (map :name fields) ["Incident"]))))

    (testing "provides specific fields for configured event source and event type"
      (let [{:keys [fields]} (s/signpost-for {:event-source :GMP :event-type :ASBO})]
        (is (= (map :name fields) ["Asbo Field"]))))

    (testing "provides specific fields for configured event source and event type when source and type are strings"
      (let [{:keys [fields]} (s/signpost-for {:event-source "GMP" :event-type "ASBO"})]
        (is (= (map :name fields) ["Asbo Field"]))))))