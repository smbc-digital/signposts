(ns visualise.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [visualise.aggregation.date-spread-tests]
            [visualise.aggregation.aggregation-tests]
            [visualise.ui.facet-tests]
            [visualise.ui.search.named-field-tests]
            [visualise.ui.search.search-control-tests]))

; run the tests from the command line with
; lein doo phantom test [auto|once]

; add tests by 'requiring them and adding them to the list below

(enable-console-print!)

(doo-tests
  'visualise.ui.facet-tests
  'visualise.ui.search.named-field-tests
  'visualise.aggregation.date-spread-tests
  'visualise.ui.search.search-control-tests
  ;'visualise.aggregation.aggregation-tests
  )

; see https://github.com/bensu/doo for more information
