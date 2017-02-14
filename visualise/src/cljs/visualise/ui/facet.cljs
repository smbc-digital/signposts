(ns visualise.ui.facet)

(defn ->cs
  ([data] (->cs data {}))
  ([data cs]
   (atom {:component-state cs
          :data            data})))

(defn checked? [!cs id]
  (get-in @!cs [:component-state id]))


(defn- cb [!cs {:keys [id name count]}]
  ^{:key (gensym)}
  [:label
   [:input {:type      :checkbox
            :value     id
            :checked   (checked? !cs id)}]
   (str name " (" count ")")])

(defn facet-tree [!cs]
  [:div.facets
   (map
     (fn [facet] (cb !cs facet))
     (:facets (:data @!cs)))])