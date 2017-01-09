(ns ingest.faking.addresses
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.string :as str]
            [faker.name :as name]
            [ingest.faking.helpers :as h]))


(def street-types ["Avenue" "Close" "Road" "Street" "Drive" "Alley" "Way" "Park"])

(def districts-and-postal-areas
  {:Adswood       3
   :Bramhall      7
   :Bredbury      6
   :Brinnington   5
   :Cheadle       8
   :Cheadle_Heath 3
   :Cheadle_Hulme 8
   :Compstall     6
   :Davenport     3
   :Edgeley       3
   :Gatley        8
   :Heaton_Chapel 4
   :Heaton_Mersey 4
   :Heaton_Moor   4
   :Heaton_Norris 4
   :Hazel_Grove   7
   :Heald_Green   8
   :High_Lane     6
   :Marple        6
   :Mellor        6
   :Offerton      2
   :Portwood      1
   :Reddish       5
   :Romiley       6
   :Stockport     1
   :Woodford      7
   :Woodley       6
   :Woodsmoor     2})

(def districts (keys districts-and-postal-areas))

(defn district-name [district]
  (str/replace (name district) "_" " "))

(defn- street-name []
  (str (name/last-name) " " (rand-nth street-types)))

(defn- street-in-district [district]
  {:street   (street-name)
   :district district})

(defn- street []
  (street-in-district (rand-nth districts)))

(def street-pool
  (take 5000 (repeatedly street)))

(defn- postcode-in-district [district]
  (str "SK" (districts-and-postal-areas district) " " (h/make "#??")))

(defn- address-in-street [street]
  (let [{:keys [street district]} street]
    {:address-1 (str (rand-int 200) " " street)
     :address-2 (district-name district)
     :town      "Stockport"
     :postcode  (postcode-in-district district)}))

(defn address-in-district [district]
  (address-in-street (street-in-district district)))

(defn stockport-street-address []
  (address-in-street (rand-nth street-pool)))




