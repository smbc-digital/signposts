(ns gov.stockport.sonar.ingest.faking.helpers
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

(defn perhaps [prob fn]
  (if (> prob (rand)) (fn)))

(defn time-in-last-3-years []
  (t/minus (t/now) (t/days (rand-int (* 3 365)))))

(defn gappy-numbers-fn [min-week-gaps]
  (fn gappy
    ([] (gappy 0))
    ([n] (lazy-seq (cons n (gappy (+ n (* min-week-gaps 7) (rand-int (* min-week-gaps 7)))))))))

(defn address-dates []
  (let [gappy (gappy-numbers-fn 26)
        start (t/minus (t/now) (t/days (* 4 365)))]
    (map (fn [n] (t/plus start (t/days n))) (rest (gappy)))))

(defn durations [min-week-gaps max-duration]
  (let [gappy (gappy-numbers-fn min-week-gaps)
        start (t/minus (t/now) (t/days (* 5 365)))]
    (map (fn [n]
           {:timestamp (t/plus start (t/days n))
            :duration  (+ 1 (rand-int max-duration))}) (rest (gappy)))))

(defn address-at [timestamp {:keys [addresses]}]
  (str/join "," (vals (:address
                  (last (filter (fn [{:keys [from]}]
                                  (t/before? from timestamp)) addresses))))))