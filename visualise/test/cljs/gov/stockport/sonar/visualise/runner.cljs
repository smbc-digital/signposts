(ns gov.stockport.sonar.visualise.runner
  (:require [doo.runner :refer-macros [doo-tests doo-all-tests]]
            [gov.stockport.sonar.visualise.ui.facet-tests]
            [gov.stockport.sonar.visualise.query.handler-tests]
            [gov.stockport.sonar.visualise.query.client-tests]
            [gov.stockport.sonar.visualise.results.selected-event-tests]
            [gov.stockport.sonar.visualise.ui.results.flot-axes-tests]
            [gov.stockport.sonar.visualise.ui.results.individual-cards-tests]
            [gov.stockport.sonar.visualise.util.ajax-tests]
            [gov.stockport.sonar.visualise.data.people-tests]
            [gov.stockport.sonar.visualise.data.merge-tests]
            [gov.stockport.sonar.visualise.data.timespan-tests]
            [gov.stockport.sonar.visualise.data.people-tests]
            [gov.stockport.sonar.visualise.util.blur-tests]
            [gov.stockport.sonar.visualise.util.date-tests]
            [gov.stockport.sonar.visualise.util.stack-tests]
            [gov.stockport.sonar.visualise.util.fmt-help-tests]
            [gov.stockport.sonar.visualise.ui.results.signposting-tests]
            [gov.stockport.sonar.visualise.ui.search.search-control-state-tests]))

; run the tests from the command line with
; lein doo phantom test [auto|once]

; add tests by 'requiring them and adding them to the list below

(enable-console-print!)

(doo-all-tests #"gov.stockport.sonar.visualise.*")

;(doo-tests 'gov.stockport.sonar.visualise.ui.results.individual-cards-tests)
