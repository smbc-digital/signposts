(ns usage.report
  (:require [clojure.string :as str]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-pdf.core :refer [pdf]])
  (:gen-class))

(comment
  "The (rough and ready) code here can be used to read the winsw.out.log produced by the visualise application and produce usage stats in terms of numbers of searches by users excluding the project team")

(def single-view-team-members-excluded-from-reporting
  #{""
    "claresudbery"
    "claudialewis"
    "danfenwick"
    "danjfenwick"
    "elastic"
    "richardfilippi"
    "rachelcobley"
    "simonestill"
    "zeshanrasul"})

(defn by-name? [q]
  (if (re-matches #".*\"name\".*" q) :Name))

(defn by-address? [q]
  (if (re-matches #".*address.*" q) :Address))

(defn by-postcode? [q]
  (if (re-matches #".*postcode.*" q) :Postcode))

(defn by-event-type? [q]
  (if (re-matches #".*event-type.*" q) :Type))

(defn by-event-source? [q]
  (if (re-matches #".*event-source.*" q) :Source))

(defn by-age-under? [q]
  (if (re-matches #".*:range.{2}\"dob\".*:lte.*" q) :AgeTo))

(defn by-age-over? [q]
  (if (re-matches #".*:range.{2}?\"dob\".*:gte.*" q) :AgeFrom))

(defn by-dob? [q]
  (if (re-matches #".*:match.{1,3}?\"dob\".*" q) :Dob))

(defn by-all? [q]
  (if (re-matches #".*\"_all\".*" q) :All))

(defn with-wildcard? [q]
  (if (re-matches #".*wildcard.*" q) :WithWildcard))

(defn query-classifier [query]
  (str/join "-"
            (sort
              (map name
                   (remove nil?
                           (map (fn [chk?] (chk? query))
                                [by-name? by-address? by-postcode? by-age-under? by-age-over? by-all? by-dob?
                                 by-event-source? by-event-type? with-wildcard?]))))))

(defn refine-stats [stats]
  (->> stats
       (map
         (fn [{:keys [date user query]}]
           (let [d (f/parse (f/formatter "yy-MM-dd") date)]
             {:date            date
              :real-date       d
              :month           (t/month d)
              :week-year       (str (t/year d) "-" (t/week-number-of-year d))
              :week-commencing (f/unparse (f/formatter "yyyy-MM-dd") (f/parse (f/formatter "yy-w") (f/unparse (f/formatter "yy-w") d)))
              :query           query
              :query-class     (query-classifier query)
              :user            (-> user
                                   (str/lower-case)
                                   (str/replace-first "[" "")
                                   (str/replace-first "]" "")
                                   (str/replace "." ""))})))
       (remove (fn [{:keys [user]}] (contains? single-view-team-members-excluded-from-reporting user)))))


(defn raw-stats [filename]
  (->> filename
       (slurp)
       (str/split-lines)
       (remove #(not (str/includes? % "performed query")))
       (map (fn [line]
              (let [tokens (str/split line #" ")
                    query (second (str/split line #"query:"))]
                {:date (first tokens) :user (nth tokens 7) :query query})))
       (refine-stats)))

(defn load-stats [filename]
  (->> filename
       (slurp)
       (str/split-lines)
       (map
         (fn [line] (zipmap [:date :user] (str/split line #","))))
       (refine-stats)))

(defn grouped-by [data keys]
  (group-by #(select-keys % keys) data))

(defn count-searches [[k v]]
  (merge k {:number-of-searches (count v)}))

(defn out-of-ten [summary]
  (let [total (apply max (cons 0 (map :number-of-searches summary)))]
    (map (fn [{:keys [number-of-searches] :as m}]
           (merge m {:poor-mans-graph (apply str (repeat (Math/round (* 10.0 (/ number-of-searches total))) "*"))}))
         summary)))

(defn summarise-by [data keys]
  (when data
    (sort-by (apply juxt keys) (out-of-ten (map count-searches (grouped-by data keys))))))

(defn pad [n]
  (fn [word]
    (apply str (take n (concat (str word) (repeat n " "))))))

(defn tabulate [data]
  (let [padder (pad 20)]
    (cons
      (str/join " " (map #(padder (name %)) (keys (first data))))
      `[~@(map
            #(str/join " " (map padder (vals %)))
            data)])))

(defn table [data]
  (let [heading-names (into [] (map name (keys (first data))))]
    `[:table {:header ~heading-names :no-split-cells? true}
      ~@(into [] (map (fn [e] (into [] (map (fn [i] [:cell (str i)]) (vals e)))) data))]))

(defn last-four-weeks [stats]
  (remove (fn [{:keys [real-date]}] (< 28 (t/in-days (t/interval real-date (t/now))))) stats))

(defn hdr [line]
  [:paragraph {:style :bold :family :courier} line])

(defn title []
  [[:heading (str "Signposts Usage at " (f/unparse (f/formatter "EEEE d MMMM yyy") (t/now)))]
   [:spacer]
   [:paragraph "based on analysis of the log files, and excluding searches performed by members of the project team"]])

(defn row [line]
  [:paragraph {:family :courier} line])

(defn chart [title data]
  [[:pagebreak]
   [:heading title]
   [:spacer]
   (if (not-empty data)
     (table data)
     [:paragraph "no data for this section"])])

(defn to-pdf [stats]
  (let [filename (str "signposts-usage-" (f/unparse (f/formatter "yyyy-MM-dd") (t/now)) ".pdf")]
    (-> `[{}
          ~@(title)
          ~@(chart "Searches by Week (most recent first)"
                   (reverse (summarise-by stats [:week-commencing])))
          ~@(chart "Searches by User  - All Time"
                   (reverse (sort-by :number-of-searches (summarise-by stats [:user]))))
          ~@(chart "Searches by User  - Last 28 Days"
                   (reverse (sort-by :number-of-searches (summarise-by (last-four-weeks stats) [:user]))))
          ~@(chart "Searches by Week by User (most recent first)"
                   (reverse (sort-by :week-commencing (summarise-by stats [:week-commencing :user]))))
          ~@(chart "Types of Searches"
                   (reverse (sort-by :number-of-searches (summarise-by (remove #(= (:query-class %) "") stats) [:query-class]))))
          ]
        (pdf filename)
        )
    (println "Usage summary saved to" filename)))

(defn -main [& args]
  (if (empty? args)
    (println "Usage: Please supply a relative path to the visualise log file e.g. ./winsw.out.log")
    (let [stats (raw-stats (first args))]
      (to-pdf stats))))