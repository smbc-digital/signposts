(ns gov.stockport.sonar.ingest.fakers.faker-test
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.spec.event-spec :as es]
            [gov.stockport.sonar.ingest.fakers.faker :as f]))

(fact "should produce default event"
      (::es/event-source (f/fake-event)) => "FAKE-SOURCE")

(fact "should allow overriding of defaults"
      (let [fake (f/fake-event {::es/event-source "OVERRIDDEN"})]
        (::es/event-source fake) => "OVERRIDDEN"
        (::es/event-type fake) => "FAKE-TYPE"))