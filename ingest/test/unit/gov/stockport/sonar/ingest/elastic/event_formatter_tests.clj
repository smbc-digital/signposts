(ns gov.stockport.sonar.ingest.elastic.event-formatter-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.elastic.event-formatter :as ef]
            [clojure.string :as str]
            [cheshire.core :refer [generate-string]]))

(def event {:event-source "SOME-SOURCE"
            :event-type   "SOME_TYPE"
            :key1         "val1"
            :key2         "val2"})

(fact "should format event for bulk indexing"
      (let [result (ef/bulk-format event)
            [line-1 line-2 :as lines] (str/split-lines result)]
        (count lines) => 2
        line-1 => (generate-string {:index {:_index "events-some-source" :_type "some-type"}})
        line-2 => (generate-string event)))

(fact "should format events for bulk indexing"
      (let [result (doall (ef/bulk-format-events [event event]))
            [line-1 line-2 :as lines] (str/split-lines result)]
        (count lines) => 4
        line-1 => (generate-string {:index {:_index "events-some-source" :_type "some-type"}})
        line-2 => (generate-string event)))