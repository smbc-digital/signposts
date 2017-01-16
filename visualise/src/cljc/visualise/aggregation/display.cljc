(ns visualise.aggregation.display)

(def days-in-year 365.25)

(defn ceil [i]
  (int (Math/ceil i)))

(def displays
  [{:display "1 month"
    :days    31}
   {:display "3 months"
    :days    93}
   {:display "1 year"
    :days    (ceil (* 1 days-in-year))}
   {:display "2 years"
    :days    (ceil (* 2 days-in-year))}
   {:display "4 years"
    :days    (ceil (* 4 days-in-year))}
   {:display "8 years"
    :days    (ceil (* 8 days-in-year))}
   {:display "16 years"
    :days    (ceil (* 16 days-in-year))}
   {:display "32 years"
    :days    (ceil (* 32 days-in-year))}])

(defn suitable-display [days-to-display]
  (first (filter #(>= (:days %) days-to-display) displays)))

(def bucket-sizes
  [{:bucket "1 day"
    :days   1}
   {:bucket "1 week"
    :days   7}
   {:bucket "1 fortnight"
    :days   14}
   {:bucket "1 month"
    :days   31}
   {:bucket "2 months"
    :days   62}
   {:bucket "3 months"
    :days   93}
   {:bucket "6 months"
    :days   186}
   {:bucket "1 year"
    :days   366}])


(defn suitable-bucket [max-buckets {suitable-days :days}]
  (first (filter
           (fn [bucket]
             (<= (/ suitable-days (:days bucket)) max-buckets))
           bucket-sizes)))

(defn display-characteristics [{days-to-display :days} max-buckets]
  (let [display-size (suitable-display days-to-display)
        days-per-bucket(:days (suitable-bucket max-buckets display-size))
        buckets (ceil (/ (:days display-size) days-per-bucket))]
    {:display-size display-size
     :number-of-buckets buckets
     :days-per-bucket days-per-bucket}))