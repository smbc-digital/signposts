(ns ingest.core-tests
  (:require [midje.sweet :refer :all]
            [ingest.core :as c]))

(fact "it should read the students file"
      (let [example (c/load-csv "resources/example.csv")]
        (first example) => {:some "1" :simple "2" :header "3" :names "4"}))

(facts "about loading csvs"
       (let [example-csv [{:a 1 :b 2} {:a 3 :b 4}]]
         (fact "it should transform csv to hashmap on supplied key"
               (c/load-csv-as-hashmap-over-key ..some-resource.. :a) => {1 {:a 1 :b 2} 3 {:a 3 :b 4}}
               (provided
                 (c/load-csv ..some-resource..) => example-csv))
         (fact "it should transform csv to hashmap on different supplied key"
               (c/load-csv-as-hashmap-over-key ..some-resource.. :b) => {2 {:a 1 :b 2} 4 {:a 3 :b 4}}
               (provided
                 (c/load-csv ..some-resource..) => example-csv))))

(facts "about joining csvs"
       (fact ""
             ()))
