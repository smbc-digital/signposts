(ns gov.stockport.sonar.visualise.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [gov.stockport.sonar.visualise.aggregation.date-spread-tests]
            [gov.stockport.sonar.visualise.aggregation.aggregation-tests]
            [gov.stockport.sonar.visualise.ui.facet-tests]
            [gov.stockport.sonar.visualise.ui.search.search-control-tests]
            [gov.stockport.sonar.visualise.query.handler-tests]
            [gov.stockport.sonar.visualise.results.selected-event-tests]))

; run the tests from the command line with
; lein doo phantom test [auto|once]

; add tests by 'requiring them and adding them to the list below

(enable-console-print!)

(doo-tests
  'gov.stockport.sonar.visualise.query.handler-tests
  'gov.stockport.sonar.visualise.ui.facet-tests
  'gov.stockport.sonar.visualise.aggregation.date-spread-tests
  'gov.stockport.sonar.visualise.results.selected-event-tests
  ;'gov.stockport.sonar.visualise.ui.search.named-field-tests
  ;'gov.stockport.sonar.visualise.ui.search.search-control-tests
  ;'gov.stockport.sonar.visualise.aggregation.aggregation-tests
  )

; see https://github.com/bensu/doo for more information
