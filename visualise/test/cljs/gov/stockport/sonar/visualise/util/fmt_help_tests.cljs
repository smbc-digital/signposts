(ns gov.stockport.sonar.visualise.util.fmt-help-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [cljs-time.core :as t]))

(deftest fmt-help-tests
  (testing "address summary"

    (is (= (fh/address-summary {}) nil))

    (is (= (fh/address-summary {:address "" :postcode ""}) nil))

    (is (= (fh/address-summary {:address "" :postcode "SK2 1AA"}) "SK2 1AA"))

    (is (= (fh/address-summary {:address  "22 Acacia Ave, Somewhere, SK2 4HI"
                                :postcode "SK2 4HI"})
           (str "22 Acacia Ave" fh/ellipsis " SK2 4HI")))

    (is (= (fh/address-summary {:address  "22, Acacia Ave, Somewhere, SK2 4HI"
                                :postcode "SK2 4HI"})
           (str "22, Acacia Ave" fh/ellipsis " SK2 4HI")))

    (is (= (fh/address-summary {:address  "22 Acacia Ave Somewhere SK2 4HI"
                                :postcode "SK2 4HI"})
           (str "22 Acacia Ave So" fh/ellipsis " SK2 4HI"))))

  (testing "date-of-birth"

    (is (= (fh/date-of-birth {}) nil))

    (is (= (fh/date-of-birth {:dob "1971-04-03"}) "3 Apr 1971")))

  (testing "to-date-ofbirth"

    (is (= (fh/to-dob "") ""))

    (is (= (fh/to-dob "1971-04-03") "3 Apr 1971")))

  (testing "readable-number"
    (is (= (fh/int-comma 1234) "1,234")))

  (testing "labels"
    (is (= (fh/label "dob") "DOB"))
    (is (= (fh/label "abc") "Abc"))
    (is (= (fh/label "Event-Type") "Event Type"))
    (is (= (fh/label "EventType") "Event Type"))
    (is (= (fh/label "EVENT_TYPE") "Event Type")))


  (testing "close-date"
    (is (= (fh/close-date "") "" ))
    (is (= (fh/close-date "25/12/2015")"Fri 25 Dec 2015" )))

  (testing "eis-close-date"
    (is (= (fh/eis-close-date "") "" ))
    (is (= (fh/eis-close-date "2015-12-25T01:00:00Z")"Fri 25 Dec 2015" ))))


