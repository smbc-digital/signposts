(ns visualise.ui.results.selected-event)

(defn selected-event [!data]
  (fn []
    (let [selected (:selected-event @!data)
          _ (println selected)]
      (when (not-empty selected)
        (let [{:keys [name dob address timestamp event-type ]} selected]
          [:div.selected-event
           [:div.panel.panel-default
            [:div.panel-heading "SELECTED EVENT"]
            [:div.panel-body
             [:p name]
             [:p dob]
             [:p address]
             [:p event-type]
             ;[:p timestamp] ; needs to be parsed back to timestamp
             ]]
           ])))))
