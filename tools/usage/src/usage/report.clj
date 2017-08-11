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


(defn refine-stats [stats]
  (->> stats
       (map
         (fn [{:keys [date user]}]
           (let [d (f/parse (f/formatter "yy-MM-dd") date)]
             {:date            date
              :real-date       d
              :month           (t/month d)
              :week-year       (str (t/year d) "-" (t/week-number-of-year d))
              :week-commencing (f/unparse (f/formatter "yyyy-MM-dd") (f/parse (f/formatter "yy-w") (f/unparse (f/formatter "yy-w") d)))
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
              (let [tokens (str/split line #" ")]
                {:date (first tokens) :user (nth tokens 7)})))
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
  (let [total (apply max (map :number-of-searches summary))]
    (map (fn [{:keys [number-of-searches] :as m}]
           (merge m {:poor-mans-graph (apply str (repeat (Math/round (* 10.0 (/ number-of-searches total))) "*"))}))
         summary)))

(defn summarise-by [data keys]
  (sort-by (apply juxt keys) (out-of-ten (map count-searches (grouped-by data keys)))))

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
  [
   [:pagebreak]
   [:heading title]
   [:spacer]
   (hdr (first (tabulate data)))
   (map row (rest (tabulate data)))])

(defn to-pdf [stats]
  (let [filename (str "signposts-usage-" (f/unparse (f/formatter "yyyy-MM-dd") (t/now)) ".pdf")]
    (pdf `[{}
           ~@(title)
           ~@(chart "Searches by Week (most recent first)"
                    (reverse (summarise-by stats [:week-commencing])))
           ~@(chart "Searches by User  - All Time"
                    (reverse (sort-by :number-of-searches (summarise-by stats [:user]))))
           ~@(chart "Searches by User  - Last 28 Days"
                    (reverse (sort-by :number-of-searches (summarise-by (last-four-weeks stats) [:user]))))
           ~@(chart "Searches by Week by User (most recent first)"
                    (reverse (sort-by :week-commencing (summarise-by stats [:week-commencing :user]))))]
         filename)
    (println "Usage summary saved to" filename)))

(defn -main [& args]
  (if (empty? args)
    (println "Usage: Please supply a relative path to the visualise log file e.g. ./winsw.out.log")
    (let [stats (raw-stats (first args))]
      (to-pdf stats))))