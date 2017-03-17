(ns gov.stockport.sonar.auth.session-manager
  (:import (java.util UUID)))

(def store (atom {}))

(defn create-session [creds]
  (let [session (str (UUID/randomUUID))]
    (swap! store assoc session creds)
    session))

(defn get-credentials [session]
  (get @store session))

(defn valid? [session]
  (contains? @store session))

(defn logout [session]
  (swap! store dissoc session))