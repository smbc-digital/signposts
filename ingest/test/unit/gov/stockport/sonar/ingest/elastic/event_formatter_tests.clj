(ns gov.stockport.sonar.ingest.elastic.event-formatter-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.elastic.event-formatter :as ef]
            [clojure.string :as str]
            [cheshire.core :refer [generate-string]]
            [pandect.algo.sha1 :refer [sha1]]))

(def event {:event-source "SOME-SOURCE"
            :event-type   "SOME_TYPE"
            :timestamp    "2017-12-28T13:14:15.123Z"
            :key1         "val1"
            :key2         "val2"})

(fact "should format event for bulk indexing"
      (let [result (ef/bulk-format event)
            [line-1 line-2 :as lines] (str/split-lines result)
            serialised-event (generate-string event)]
        (count lines) => 2
        line-1 => (generate-string {:index {:_index "events-some-source" :_type "some-type" :_id (sha1 serialised-event)}})
        line-2 => serialised-event))

(fact "should format events for bulk indexing"
      (let [result (doall (ef/bulk-format-events [event event]))
            [line-1 line-2 line-3 :as lines] (str/split-lines result)
            serialised-event (generate-string event)]
        (count lines) => 4
        line-1 => (generate-string {:index {:_index "events-some-source" :_type "some-type" :_id (sha1 serialised-event)}})
        line-2 => serialised-event
        line-3 => (generate-string {:index {:_index "events-some-source" :_type "some-type" :_id (sha1 serialised-event)}})))