(ns ingest.faking.helpers
  (:require [clojure.string :as str]
            [clj-time.core :as t]))

(def ^{:private true} alphabet (seq "abcdefghijklmnopqrstuvwxyz"))

(defn- numerify [& formats]
  (str/replace (rand-nth formats)
               #"#"
               (fn [_] (str (rand-int 10)))))

(defn- letterify [& formats]
  (str/replace (rand-nth formats)
               #"\?"
               (fn [_] (str (rand-nth alphabet)))))

(defn make [template]
  (str/upper-case (letterify (numerify template))))

(defn weighted-random-fn [weighted-tuples]
  (fn []
    (first (shuffle (apply concat
                           (map
                             (fn [[weight choice]] (repeat weight choice))
                             weighted-tuples))))))

(defn up-to [number fn]
  (take (+ 1 (rand-int number)) (repeatedly fn)))

(defn age-in-years [dob]
  (t/in-years (t/interval dob (t/now))))