(ns ingest.faking.schools
  (:require [ingest.faking.addresses :as address]
            [ingest.faking.people :as people]
            [ingest.faking.phone :as phone]
            [ingest.faking.helpers :as h]
            [clj-time.core :as t]))

(defn- school [[name district]]
  {:name        name
   :phone       (phone/phone-number)
   :district    district
   :address     (address/address-in-district district)
   :headteacher (people/adult)})

(defn schools [school-type]
  (map
    (fn [district]
      (school
        [(str (address/district-name district) " " school-type)
         district]))
    address/districts))

(def primary-schools
  (schools "Primary School"))

(def infant-schools
  (schools "Infant School"))

(def junior-schools
  (schools "Junior School"))

(defn for-district [schools district]
  (filter #(= district (:district %)) schools))

(def secondary-schools
  (map school
       [["Bramhall High School" :Bramhall]
        ["Cheadle Hulme High School" :Cheadle_Hulme]
        ["Harrytown Catholic High School" :Romiley]
        ["Hazel Grove High School" :Hazel_Grove]
        ["The Kingsway School" :Cheadle]
        ["Marple Hall School" :Marple]
        ["Priestnall School" :Heaton_Mersey]
        ["Reddish Vale High School" :Reddish]
        ["St Anne's RC High School" :Heaton_Chapel]
        ["St James' RC High School" :Cheadle_Hulme]
        ["Stockport Academy" :Cheadle_Heath]
        ["Stockport Technical School", :Stockport]
        ["Stockport School" :Heaviley]
        ["Werneth School" :Romiley]]))

(defn schools-for-district [district]
  {:primary   (for-district primary-schools district)
   :infant    (for-district infant-schools district)
   :junior    (for-district junior-schools district)
   :secondary (rand-nth secondary-schools)})

(defn period [dob start end]
  (let [from (t/date-midnight (t/year (t/plus dob (t/years start))) 9)
        to (t/date-midnight (t/year (t/plus from (t/years (- end start)))) 6 30)]
    {:from   from
     :to     to}))

(defn with-infant-education [{:keys [dob] :as child} schools age]
  (if (> age 3)
    (assoc child :schooling (assoc (period dob 3 7) :school (:infant schools)))
    child))

(defn with-junior-education [{:keys [dob] :as child} schools age]
  (if (> age 7)
    (update child :schooling
            (fn [schooling]
              (cons schooling (assoc (period dob 7 11) :school (:junior schools)))))
    child))

(defn with-secondary-education [{:keys [dob] :as child} schools age]
  (if (> age 11)
    (update child :schooling
            (fn [schooling]
              (cons schooling (assoc (period dob 11 16) :school (:secondary schools)))))
    child))

(defn schooling-for-child [schools {:keys [dob] :as child}]
  (let [age (h/age-in-years dob)]
  (-> child
      (with-infant-education schools age)
      (with-junior-education schools age)
      (with-secondary-education schools age))))

(defn with-schools-for [district dependents]
  (let [schools (schools-for-district district)]
    (map (fn [child] (assoc child :schooling (schooling-for-child schools child))) dependents)))

(defn with-schooling [{:keys [district dependents] :as household}]
  (assoc household :dependents (with-schools-for district dependents)))

