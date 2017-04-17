(ns gov.stockport.sonar.ingest.helper.dates-tests
  (:require [midje.sweet :refer :all]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [gov.stockport.sonar.ingest.helper.dates :as d]
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

(defmacro bench
  "Times the execution of forms, discarding their output and returning
a long in nanoseconds."
  ([& forms]
   `(let [start# (System/nanoTime)]
      ~@forms
      (- (System/nanoTime) start#))))

(def form-1 "1995-11-19 12:12:12")
(def form-2 "1995-11-19T12:12:12")

(defn time-parser [parse-fn input]
  (let [nanos (bench (count (map parse-fn (take 500 input))))]
    (println (/ nanos 1000000.0))
    nanos))

(defn time-parser-against-single-format [parse-fn]
  (time-parser parse-fn (repeat form-1)))

(defn time-parser-against-multiple-formats [parse-fn]
  (time-parser parse-fn (interleave (repeat form-2) (repeat form-1))))

(fact "should parse test forms"
      (d/parse form-1) => (t/date-time 1995 11 19 12 12 12)
      (d/parse form-2) => (t/date-time 1995 11 19 12 12 12))

(fact "should parse dates efficiently when similar format presented"
      (< (time-parser-against-single-format d/parse) (time-parser-against-single-format f/parse)))

(fact "should parse dates efficiently when more than one format presented"
      (< (time-parser-against-multiple-formats d/parse) (time-parser-against-multiple-formats f/parse)))
