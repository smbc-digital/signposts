(ns ingest.faking.schools
  (:require [ingest.faking.addresses :as address]
            [ingest.faking.people :as people]
            [ingest.faking.phone :as phone]
            [ingest.faking.helpers :as h]
            [clj-time.core :as t]))

(def schools (atom {}))

(defn- school [[name district]]
  (swap! schools assoc name
         {:name        name
          :phone       (phone/phone-number)
          :district    district
          :address     (address/address-in-district district)
          :headteacher (people/employee)})
  (get @schools name))

(defn create-schools-of-type [school-type]
  (doall (map
           (fn [district]
             (school
               [(str (address/district-name district) " " school-type)
                district]))
           address/districts)))

(def infant-schools
  (create-schools-of-type "Infant School"))

(def junior-schools
  (create-schools-of-type "Junior School"))

(defn for-district [schools district]
  (:name (first (filter #(= district (:district %)) schools))))

(def secondary-schools
  (doall (map school
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
               ["Werneth School" :Romiley]])))

(defn schools-for-district [district]
  {:infant    (for-district infant-schools district)
   :junior    (for-district junior-schools district)
   :secondary (:name (rand-nth secondary-schools))})

(defn attendance [dob start end]
  (let [from (t/date-midnight (t/year (t/plus dob (t/years start))) 9)
        to (t/date-midnight (t/year (t/plus from (t/years (- end start)))) 6 30)]
    {:from from
     :to   to}))

(defn with-infant-education [{:keys [dob]} schools age]
  (if (> age 2)
    (assoc (attendance dob 3 7) :school (:infant schools))))

(defn with-junior-education [{:keys [dob]} schools age]
  (if (> age 7)
    (assoc (attendance dob 7 11) :school (:junior schools))))

(defn with-secondary-education [{:keys [dob]} schools age]
  (if (> age 11)
    (assoc (attendance dob 11 16) :school (:secondary schools))))

(defn schooling-for-child [schools {:keys [dob] :as child}]
  (let [age (h/age-in-years dob)]
    (filter #(not (nil? %))
            [(with-infant-education child schools age)
             (with-junior-education child schools age)
             (with-secondary-education child schools age)])))

(defn with-schools-for [district dependents]
  (let [schools (schools-for-district district)]
    (map (fn [child] (assoc child :schooling (schooling-for-child schools child))) dependents)))

(defn with-schooling [{:keys [district dependents] :as household}]
  (assoc household :dependents (with-schools-for district dependents)))

