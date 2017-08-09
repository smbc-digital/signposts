(ns gov.stockport.sonar.esproxy.es-query-parser-test
    (:require  [gov.stockport.sonar.esproxy.es-query-parser :as es-query-parser]
               [midje.sweet :refer :all]
    ))

(fact "should split query into tokens"
      (es-query-parser/query-tokens "now is the winter of our discon*")
       => ["now" "is" "the" "winter" "of" "our" "discon*"])


(fact "should recongnize wildcards"
      (es-query-parser/wild-card? "token") => false
      (es-query-parser/wild-card? "token*") => true
  )

(fact "should parse token"
      (es-query-parser/parse-token :some-field "mar*") => {:wildcard {:some-field "mar*"}}
      (es-query-parser/parse-token :some-field "marsden") => {:match {:some-field "marsden"}}
      )

(fact  "should generate a list of tokens form a query"
       (es-query-parser/parse-tokens :some-field "j* smith") =>
          [{:wildcard {:some-field "j*"}} {:match {:some-field "smith"}}]
       )

(fact "should generate a valid query for a query string and field"
      (es-query-parser/parse-query :some-field "j* smith") =>
      {
                    :should
                    [
                     {:wildcard {:some-field "j*"}}
                     {:match {:some-field "smith"}}
                     ]
                    :minimum_should_match 2
       }
      )