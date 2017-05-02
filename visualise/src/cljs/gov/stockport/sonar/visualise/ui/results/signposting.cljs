(ns gov.stockport.sonar.visualise.ui.results.signposting)

(def signposts
  {:default {:default {:fields [{:name    "Name"
                                 :default "(who you should speak to)"}
                                {:name    "Phone"
                                 :default "(their phone number)"}
                                {:name    "Ref#"
                                 :default "(reference for this event)"}
                                {:name    "Notes"
                                 :default "You are seeing default information for this event because a suitable signpost has not been configured for this event source and type"}]}}
   :SCHOOLS {:default {:fields   [{:name   "Headteacher"
                                   :source :school-headteacher}
                                  {:name   "School Phone"
                                   :source :school-phone}
                                  {:name   "Pupil"
                                   :source :name}
                                  {:name    "Notes"
                                   :default "Follow up with the school"}]}}})

(defn signpost-for [{:keys [event-source event-type] :as event}]
  {:fields
   (let [source (keyword event-source)
         type (keyword event-type)
         definitions (or (get-in signposts [source type])
                         (get-in signposts [source :default])
                         (get-in signposts [:default :default]))]
     (map
       (fn [{:keys [name default source]}]
         (println name event)
         {:name  name
          :value (get event source default)})
       (:fields definitions)))})
