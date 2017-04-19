(ns gov.stockport.sonar.auth.session-manager
  (:require [clj-time.core :as t]
            [gov.stockport.sonar.auth.crypto :as crypto])
  (:import (java.util UUID)))

(def max-session-idle-minutes 15)

(def store (atom {}))

(defn session-id []
  (str (UUID/randomUUID)))

(defn- with-password [session-data encrypted-password]
  (assoc session-data :password encrypted-password))

(defn- with-expiry [session-data]
  (assoc session-data :expiry (t/plus (t/now) (t/minutes max-session-idle-minutes))))

(defn- not-yet-expired? [{:keys [expiry]}]
  (t/before? (t/now) expiry))

(defn- remove-existing-sessions-for-user [{existing-username :username}]
  (swap! store (fn [store] (reduce merge {} (filter (fn [[_ {:keys [username]}]] (not (= username existing-username))) store)))))

(defn create-session [{:keys [username password] :as creds}]
  (remove-existing-sessions-for-user creds)
  (let [session-id (session-id)]
    (let [{:keys [secret-key ciphertext]} (crypto/encrypt password)]
      (swap! store assoc session-id (-> creds
                                        (with-password ciphertext)
                                        (with-expiry)))
      {:username    username
       :session-id  session-id
       :session-key secret-key})))

(defn valid? [{:keys [session-id]}]
  (and
    (not (nil? session-id))
    (contains? @store session-id)
    (not-yet-expired? (get @store session-id))))

(defn get-credentials [{:keys [session-id session-key] :as session}]
  (when (valid? session)
    (swap! store update-in [session-id] with-expiry)
    (let [{username :username encrypted-password :password} (get @store session-id)]
      {:username username
       :password (crypto/decrypt session-key encrypted-password)})))

(defn logout [{:keys [session-id]}]
  (swap! store dissoc session-id))