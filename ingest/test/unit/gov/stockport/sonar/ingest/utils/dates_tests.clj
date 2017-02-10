(ns gov.stockport.sonar.ingest.utils.dates-tests
  (:require [midje.sweet :refer :all]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [gov.stockport.sonar.ingest.util.dates :as d]
            [midje.checking.core :as checking]))

(def expected-date (t/date-time 1995 11 19))

(defn parses? [val]
  (fn [fmt]
    (let [result (try (f/parse fmt val) (catch Exception _ nil))]
    (or (= result expected-date)
      (checking/as-data-laden-falsehood {:notes [(str "Failed for " val " produced [" result "]")]})))))

(fact "should return matching format for date"
      (d/format-for "1995-11-19") => (parses? "1995-11-19")
      (d/format-for "19-11-1995") => (parses? "19-11-1995")
      (d/format-for "19-11-95") => (parses? "19-11-95")
      (d/format-for "19/11/1995") => (parses? "19/11/1995")
      (d/format-for "19/11/95") => (parses? "19/11/95")
      (d/format-for "19-Nov-95") => (parses? "19-Nov-95")
      (d/format-for "19-Nov-1995") => (parses? "19-Nov-1995")
      (d/format-for "19-nov-1995") => (parses? "19-nov-1995")
      (d/format-for "19-nov-1995") => (parses? "19-nov-1995")
      (d/format-for "19-NOV-1995") => (parses? "19-NOV-1995"))

(defn time-parser-against-single-format [parse-fn]
  (time (count (map parse-fn (take 500 (repeat "1995-11-19 12:12:12"))))))

(defn time-parser-against-multiple-formats [parse-fn]
  (time (count (map parse-fn (take 500 (interleave (repeat "1995-11-19T12:12:12") (repeat "1995-11-19 12:12:12")))))))

(fact "should parse dates efficiently when similar format presented"
      (< (time-parser-against-single-format d/parse) (time-parser-against-single-format f/parse)))

(fact "should parse dates efficiently when more than one format presented"
      (< (time-parser-against-multiple-formats d/parse) (time-parser-against-multiple-formats f/parse)))
