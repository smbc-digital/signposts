(ns gov.stockport.sonar.ingest.helper.address-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.helper.address :as address]))

(fact "should handle nil postcodes"
      (address/postcode nil) => nil)

(fact "should extract postcode from string"
      (address/postcode "123 Stockport Road") => nil
      (address/postcode "123 Stockport Road, SK1 1AB") => "SK1 1AB"
      (address/postcode "123 Stockport Road, SK1 1AB, England") => "SK1 1AB"
      (address/postcode "123 Stockport Road, SK1 1ABI") => "SK1 1AB"
      (address/postcode "123 Stockport Road, 123SK1 1AB") => "SK1 1AB"
      (address/postcode "SK1 1AB") => "SK1 1AB")

(fact "ignores mixed or lowercase postcodes"
      (address/postcode "123 Stockport Road, 123sk1 1ab") => nil)

(fact "can modify address with postcode in place"
      (address/->title-case-address nil) => nil
      (address/->title-case-address "") => ""
      (address/->title-case-address "123 STOCKPORT ROAD, SK1 1AB, UNITED KINGDOM") => "123 Stockport Road, SK1 1AB, United Kingdom"
      (address/->title-case-address "123 STOCKPORT ROAD, SK1 1AB, UK") => "123 Stockport Road, SK1 1AB, UK"
      (address/->title-case-address "123 STOCKPORT ROAD, SK1 1AB UNITED KINGDOM") => "123 Stockport Road, SK1 1AB United Kingdom"
      (address/->title-case-address "123 STOCKPORT ROAD, SK1 1AB") => "123 Stockport Road, SK1 1AB"
      (address/->title-case-address "123 STOCKPORT ROAD, UNITED KINGDOM") => "123 Stockport Road, United Kingdom"
      (address/->title-case-address "SK1 3AB, UNITED KINGDOM") => "SK1 3AB, United Kingdom")