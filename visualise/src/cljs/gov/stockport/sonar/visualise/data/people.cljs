(ns gov.stockport.sonar.visualise.data.people
  (:require [gov.stockport.sonar.visualise.data.colours :as c]
            [gov.stockport.sonar.visualise.util.stack :as s]
            [gov.stockport.sonar.visualise.data.merge :as merge]
            [gov.stockport.sonar.visualise.query.client :refer [keep-alive]]
            [clojure.string :as str]
            [gov.stockport.sonar.visualise.data.timespan :as timespan]))

(def group-keys [:name :dob])

(defn- all-event-data [[_ {:keys [data]}]]
  data)

(defn- highlighted-event-data [[_ {:keys [highlighted? data]}]]
  (when highlighted? data))

(defn- locked-event-data [[_ {:keys [locked? data]}]]
  (when locked? data))

(defn- extractor-for [extractor]
  (fn [{:keys [people]}]
    (reduce concat [] (map extractor people))))

(def all-events (extractor-for all-event-data))

(def highlighted-events (extractor-for highlighted-event-data))

(def locked-events (extractor-for locked-event-data))

(defn lock [data pkey]
  (assoc-in data [:people pkey :locked?] true))

(defn unlock [data pkey]
  (assoc-in data [:people pkey :locked?] false))

(defn toggle-lock-person [data pkey]
  (update-in data [:people pkey :locked?] not))

(defn locked-people [{:keys [people]}]
  (reduce merge {} (filter (fn [[_ pdata]] (:locked? pdata)) people)))

(defn by-people [{:keys [result] :as data}]
  (let [existing-people (locked-people data)
        new-people (reduce merge {}
                           (map (fn [[k v]] {k {:data v}})
                                (group-by
                                  (fn [m] (select-keys m group-keys))
                                  (merge/merge-events (locked-events data) result))))]
    (assoc data :people (merge/merge-people-flags existing-people new-people))))

(defn with-max-score [{:keys [people] :as data}]
  (assoc data :people
              (reduce merge {}
                      (map (fn [[k v]] {k (assoc v :score (apply max (map :score (:data v))))}) people))))

(def area #(first (str/split (or (:postcode %) "") #"--")))

(defn with-areas [{:keys [people] :as data}]
  (assoc data :people
              (reduce merge {}
                      (map (fn [[k v]] {k (assoc v :areas (into #{} (remove str/blank? (map area (:data v)))))}) people))))

(def event-type #(first (str/split (or (:event-type %) "") #"--")))

(defn with-event-types [{:keys [people] :as data}]
  (assoc data :people
              (reduce merge {}
                      (map (fn [[k v]] {k (assoc v :event-types (into #{} (remove str/blank? (map event-type (:data v)))))}) people))))

(defn- surname[person-key]
  (let [name-components (str/split (:name person-key) #"\s+")]
    (if(= nil (last name-components)))
      (str/lower-case(first name-components))
      (str/lower-case(last name-components))))

(defn- forename [person-key]
  (let [name-components (str/split (:name person-key) #"\s+")]
  (if(= nil (re-find #"(?i)mr|mrs|miss|ms|dr"(first name-components)))
    (str/lower-case(first name-components))
    (str/lower-case(second name-components)))))


(defn is-locked?[p]
  (if (:locked? p) 0 1))

(defn with-relevance-rank
  ([{:keys [people] :as data}]
   (assoc data :people
               (reduce merge {}
                       (map-indexed
                         (fn [idx [k v]] {k (assoc v :relevance-rank (+ 1 idx))})
                         (sort-by (fn [[person-key p]] [ (is-locked? p) (- 0 (:score p)) (- 0 (:score p)) (surname person-key) (forename person-key)]) people))))))

(defn with-name-rank
  ([{:keys [people] :as data}]
   (assoc data :people
               (reduce merge {}
                       (map-indexed
                         (fn [idx [k v]] {k (assoc v :name-rank (+ 1 idx))})
                         (sort-by (fn [[person-key p]] [(is-locked? p) (surname person-key) (forename person-key)]) people))))))


(def locked-then-score-then-surname
  (fn [[_ p]] [(if (:locked? p) 0 1) (- 0 (:score p)) (surname p)]))

(defn with-rank [{:keys [people] :as data}]
  (assoc data :people
              (reduce merge {}
                      (map-indexed
                        (fn [idx [k v]] {k (assoc v :rank (+ 1 idx))})
                        (sort-by locked-then-score-then-surname people)))))

(defn highlight-person [{:keys [color-mgr] :as data} pkey]
  (let [{:keys [assign available?]} color-mgr]
    (-> data
        (update-in [:people pkey] #(-> %
                                       (assoc :highlighted? true)
                                       (assoc :color (assign pkey))))
        (assoc :highlighting-allowed? (available?)))))

(defn un-highlight-person [{:keys [color-mgr] :as data} pkey]
  (let [{:keys [release available?]} color-mgr]
    (release pkey)
    (-> data
        (update-in [:people pkey] #(-> %
                                       (assoc :highlighted? false)
                                       (dissoc :color)))
        (assoc :highlighting-allowed? (available?)))))

(defn toggle-highlight-person [{:keys [people] :as data} pkey]
  (let [person (get people pkey)
        toggle-fn (if (:highlighted? person) un-highlight-person highlight-person)]
    (toggle-fn data pkey)))

(defn- assoc-once [m k v]
  (if (contains? m k) m (assoc m k v)))

(defn clean-up-any-previously-locked-colours [{:keys [color-mgr] :as data}]
  (let [{:keys [release available?]} color-mgr]
    (doseq [[pkey {:keys [highlighted? locked?]}] (:people data)]
      (if (and highlighted? (not locked?)) (release pkey)))
    (assoc data :highlighting-allowed? (available?))))

(defn with-colours [data]
  (-> data
      (assoc-once :highlighting-allowed? true)
      (assoc-once :color-mgr (s/new-colour-manager c/colour-priority))
      (clean-up-any-previously-locked-colours)))

(defn with-timespan [data]
  (assoc data :timespan (timespan/from-events (all-events data))))

(defn toggle-sort-by[data]
  (if(= :by-relevance (get data :rank-by))
    (assoc data :rank-by :by-name)
    (assoc data :rank-by :by-relevance)))

(defn from-data [data]
  (-> data
      (assoc :rank-by :by-relevance)
      (dissoc :selected-event)
      (with-colours)
      (by-people)
      (with-max-score)
      (with-areas)
      (with-event-types)
      (with-timespan)
      (with-name-rank)
      (with-relevance-rank)
      ))

(defn sort-as [{:keys [rank-by people] as :data}]
  (if (= rank-by :by-name)
    (sort-by (fn [[_ {:keys [name-rank]}]] name-rank) people)
    (sort-by (fn [[_ {:keys [relevance-rank]}]] relevance-rank) people)
    ))

(defn sort-by-relevance[data]
  (= :by-relevance (get data :rank-by)))

(defn reset-selection [data]
  (reduce (fn [d pkey] (un-highlight-person d pkey)) data (keys (:people data))))

(defn by-rank [{:keys [people]}]
  (sort-by (fn [[_ {:keys [rank]}]] rank) people))

(defn results-summary [data]
  (defn num-people-summary [data]
    (let [num-people (count (:people data))]
      (str num-people (if (> num-people 1) " people" " person"))))
  (defn num-events-summary [data]
    (str (:total data) " event" (if (> (:total data) 1) "s")))
  (if (> (:total data) 0)
    (str "Your search returned " (num-events-summary data) " concerning " (num-people-summary data))
    "Sorry, that search returned no results"))

(defn- clear-selected-event? [people]
  (reduce merge {}
          (map (fn [[k v]] {k (dissoc v :has-selected-event?)}) people)))


(defn deselect-event [{:keys [people] :as data}]
  (-> data
      (dissoc :selected-event)
      (assoc :people (clear-selected-event? people))))

(defn toggle-event [{:keys [people selected-event] :as data} event]
  (if (= selected-event event)
    (deselect-event data)
    (let [pkey-for-event (select-keys event group-keys)]
      (-> data
          (assoc :selected-event event)
          (assoc :people (clear-selected-event? people))
          (assoc-in [:people pkey-for-event :has-selected-event?] true)))))
