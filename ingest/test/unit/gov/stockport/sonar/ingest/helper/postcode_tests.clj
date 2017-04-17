(ns gov.stockport.sonar.ingest.helper.postcode-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.helper.postcode :as postcode]))

(fact "should handle nil postcodes"
      (postcode/extract nil) => nil)

(fact "should extract postcode from string"
      (postcode/extract "123 Stockport Road, SK1 1AB") => "SK1 1AB"
      (postcode/extract "123 Stockport Road, SK1 1AB, England") => "SK1 1AB"
      (postcode/extract "123 Stockport Road, SK1 1ABI") => "SK1 1AB"
      (postcode/extract "123 Stockport Road, 123SK1 1AB") => "SK1 1AB"
      (postcode/extract "SK1 1AB") => "SK1 1AB")

(fact "ignores mixed or lowercase postcodes"
      (postcode/extract "123 Stockport Road, 123sk1 1ab") => nil)

