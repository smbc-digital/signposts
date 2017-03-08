(ns gov.stockport.sonar.esproxy.routes-tests
  (:require [midje.sweet :refer :all]
            [bidi.bidi :refer [match-route]]
            [gov.stockport.sonar.esproxy.routes :refer [api]]))

(facts "about routes"

       (fact "should match elastic search query route"
             (:handler (match-route api "/query" :request-method :post)) => :query)

       (fact "should produce 404 for missing route"
             (match-route api "/wibble") => {:handler :404}))
