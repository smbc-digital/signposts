(ns gov.stockport.sonar.visualise.ui.results.signposting
  (:require [gov.stockport.sonar.visualise.state :refer [!signposting-config]]))

(defn signpost-for [{:keys [event-source event-type] :as event}]
  {:fields
   (let [signposts @!signposting-config
         source (keyword event-source)
         type (keyword event-type)
         definitions (or (get-in signposts [source type])
                         (get-in signposts [source :default])
                         (get-in signposts [:default :default]))]
     (map
       (fn [{:keys [name default source]}]
         {:name  name
          :value (get event (keyword source) default)})
       (:fields definitions)))})
