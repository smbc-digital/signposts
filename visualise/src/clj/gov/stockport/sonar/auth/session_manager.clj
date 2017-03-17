(ns gov.stockport.sonar.auth.session-manager
  (:require [gov.stockport.sonar.auth.crypto :as crypto])
  (:import (java.util UUID)))

(def store (atom {}))

(defn session-id []
  (str (UUID/randomUUID)))

(defn create-session [{:keys [username password] :as creds}]
  (let [session-id (session-id)]
    (let [{:keys [secret-key ciphertext]} (crypto/encrypt password)]
      (swap! store assoc session-id (assoc creds :password ciphertext))
      {:username    username
       :session-id  session-id
       :session-key secret-key})))

(defn get-credentials [{:keys [session-id session-key]}]
  (if-let [{username :username encrypted-password :password} (get @store session-id)]
    {:username username
     :password (crypto/decrypt session-key encrypted-password)}))

(defn valid? [{:keys [session-id]}]
  (contains? @store session-id))

(defn logout [{:keys [session-id]}]
  (swap! store dissoc session-id))