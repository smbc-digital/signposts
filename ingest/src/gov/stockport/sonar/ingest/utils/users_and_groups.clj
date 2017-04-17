(ns gov.stockport.sonar.ingest.utils.users-and-groups
  (:require [gov.stockport.sonar.ingest.client.elastic-search-client :as esc]
            [gov.stockport.sonar.ingest.utils.fake-data :refer [event-sources]]
            [clojure.string :as str]))


(defn esname [keyword]
  (str/lower-case (name keyword)))

(def common-password "password123")

(defn create-user [{:keys [user-name user-template]}]
  (let [user-path (str "/_xpack/security/user/" user-name)]
    (try (esc/delete-to-es {:path user-path}) (catch Exception _))
    (esc/post-json-to-es {:path    user-path
                          :payload user-template})))

(defn create-role [{:keys [role-name role-template]}]
  (esc/post-json-to-es {:path    (str "/_xpack/security/role/" role-name)
                        :payload role-template}))

(defn ro-role-name [event-source]
  (str (esname event-source) "-read"))

(defn feed-name [event-source]
  (str "feed_" (esname event-source)))

(defn read-event-source-role [event-source]
  {:role-name     (ro-role-name event-source)
   :role-template {:indices [{:names          [(feed-name event-source)]
                              :privileges     ["read"]
                              :field_security {:grant ["*"]}}]}})

(defn read-event-source-user [event-source]
  (let [username (str (esname event-source) "-read")]
    {:user-name     username
     :user-template {:username username
                     :password common-password
                     :roles    [(ro-role-name event-source)]}}))

(defn read-all-user [event-source]
  (let [username (str (esname event-source) "-read")]
    {:user-name     username
     :user-template {:username username
                     :password username
                     :roles    [(ro-role-name event-source)]}}))

(defn event-source-names []
  (map :event-source event-sources))

(defn create-ro-roles []
  (doall (map #(create-role (read-event-source-role %)) (event-source-names))))

(defn create-ro-users []
  (doall (map #(create-user (read-event-source-user %)) (event-source-names))))

(def full-access "full-access")

(defn create-full-role []
  (create-role {:role-name     full-access
                :role-template {:indices [{:names          ["feed_*"]
                                           :privileges     ["read"]
                                           :field_security {:grant ["*"]}}]}}))

(defn create-full-user []
  (create-user {:user-name     full-access
                :user-template {:username full-access
                                :password common-password
                                :roles    [full-access]}}))

(defn create-demo-users-and-groups []
  (println "creating users and roles")
  (create-ro-roles)
  (create-ro-users)
  (create-full-role)
  (create-full-user))

