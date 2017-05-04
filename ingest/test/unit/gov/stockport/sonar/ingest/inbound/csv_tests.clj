(ns gov.stockport.sonar.ingest.inbound.csv-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.inbound.csv :as csv]))

(facts "csv mapper"

       (fact "can provide mapped data"
             ((csv/mapper "some") 1 "data") => {:line-number 1
                                                :data        {:some "data"}})

       (fact "can handle multiple columns"
             ((csv/mapper "some,more,columns") 2 "data1,data2,data3") => {:line-number 2
                                                                          :data        {:some    "data1"
                                                                                        :more    "data2"
                                                                                        :columns "data3"}})

       (fact "can handle blank columns"
             ((csv/mapper "some,more,columns") 3 "data1,,data3") => {:line-number 3
                                                                     :data        {:some    "data1"
                                                                                   :more    ""
                                                                                   :columns "data3"}})

       (fact "does not presently handle missing headers"
             ((csv/mapper "some,,columns") & anything)
             =>
             (throws IllegalStateException "Blank values not allowed in file header"))

       (fact "does not presently handle commas in quoted headers"
             ((csv/mapper "some,\"strange,heading\"") & anything)
             =>
             (throws IllegalStateException "Comma delimited headings not allowed in file header"))

       (fact "will reject data if expected columns don't match"
             ((csv/mapper "some,columns") 1 "data") => {:line-number 1
                                                        :error       :insufficient-data
                                                        :data        {}})

       (fact "handles optionally quoted data"
             ((csv/mapper "some,\"more\",columns") 3 "data1,\"wibble, mcping\",data3")
             => {:line-number 3
                 :data        {:some    "data1"
                               :more    "wibble, mcping"
                               :columns "data3"}})

       (fact "kebab-cases the column keys"
             ((csv/mapper "some-Interesting,\"more data\",ColumnSettings,with_underscores") 3 "data1,\"wibble mcping\",data3,argh")
             => {:line-number 3
                 :data        {:some-interesting "data1"
                               :more-data        "wibble mcping"
                               :column-settings  "data3"
                               :with-underscores "argh"}}))