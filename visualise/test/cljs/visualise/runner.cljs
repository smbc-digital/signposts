(ns visualise.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [visualise.aggregation.date-spread-tests]))

; run the tests from the command line with
; lein doo phantom test [auto|once]

; add tests to the list below

(doo-tests 'visualise.aggregation.date-spread-tests
           ;'more.tests.namespaces.can.be.added.here
           )

; see https://github.com/bensu/doo for more information
