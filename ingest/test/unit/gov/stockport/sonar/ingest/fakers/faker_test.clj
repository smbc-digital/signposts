(ns gov.stockport.sonar.ingest.fakers.faker-test
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.fakers.faker :as f]))

(fact "should produce default event"
      (:event-source (f/fake-event)) => "FAKE-SOURCE")

(fact "should allow overriding of defaults"
      (let [fake (f/fake-event {:event-source "OVERRIDDEN"})]
        (:event-source fake) => "OVERRIDDEN"
        (:event-type fake) => "FAKE-TYPE"))