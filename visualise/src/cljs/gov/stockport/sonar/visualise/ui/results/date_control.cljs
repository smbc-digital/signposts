(ns gov.stockport.sonar.visualise.ui.results.date-control
  (:require [reagent.core :as r]
            [cljs-time.format :as tf]
            [clojure.string :as str]))

(defn convert-time-str-to-obj [time-as-str]
  (cljs-time.core/date-time
    (js/parseInt (apply str (subvec (str/split time-as-str "") 0 4)))
    (js/parseInt (apply str (subvec (str/split time-as-str "") 5 6)))
    (js/parseInt (apply str (subvec (str/split time-as-str "") 9 10)))
    ))


(defn start-selector [!data]
  [:div.form.left-date-selector
   "From"
   [:input {:name "start date"
            :type "date"
            :on-change
                  #(swap! !data (fn [data] (assoc-in data [:timespan :from-date] (convert-time-str-to-obj (-> % .-target .-value)))))}]])


(defn end-selector [!data]
  [:div.form.right-date-selector.pull-right
   "To"
   [:input {:name "start date"
            :type "date"
            :on-change
                  #(swap! !data (fn [data] (assoc-in data [:timespan :to-date] (convert-time-str-to-obj (-> % .-target .-value)))))}]])


