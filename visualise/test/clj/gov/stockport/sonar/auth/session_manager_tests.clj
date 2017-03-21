(ns gov.stockport.sonar.auth.session-manager-tests
  (:require [midje.sweet :refer :all]
            [clj-time.core :as t]
            [gov.stockport.sonar.test-clock :as test-clock]
            [gov.stockport.sonar.auth.session-manager :as s]
            [gov.stockport.sonar.auth.crypto :as crypto]))

(against-background
  [(before :facts (reset! s/store {}))]

  (facts "about session manager"

         (fact "sessions use uuid's for session-id"
               (:session-id (s/create-session {})) => "some-uuid"
               (provided
                 (s/session-id) => "some-uuid"))

         (fact "session id includes the supplied username"
               (:username (s/create-session {:username "wibble"})) => "wibble")

         (fact "create session returns unique session identifier"
               (let [{session-1 :session-id} (s/create-session {})
                     {session-2 :session-id} (s/create-session {})]
                 (= session-1 session-2) => false))

         (fact "non existent sessions are not valid"
               (s/valid? nil) => false
               (s/valid? {}) => false)

         (fact "returns nil credentials for non-existant session"
               (s/get-credentials "dunno") => nil)

         (fact "returns indicator that session is valid"
               (let [session (s/create-session {})]
                 (s/valid? session) => true
                 (s/valid? "non-existent-session") => false))

         (fact "passwords are encrypted and the key is returned with the session details"
               (let [plaintext-password-capture (atom nil)]
                 (with-redefs [s/session-id (fn [] ..uuid..)
                               crypto/encrypt (fn [plaintext-password]
                                                (reset! plaintext-password-capture plaintext-password)
                                                {:secret-key ..secret-key..
                                                 :ciphertext ..encrypted-password..})]
                   (let [session (s/create-session {:password "the-password"})]
                     (:session-id ..uuid..)
                     (:session-key session) => ..secret-key..
                     (:password (get @s/store ..uuid..)) => ..encrypted-password..
                     @plaintext-password-capture => "the-password"))))

         (fact "given existing session retrieves credentials"
               (let [session (s/create-session {:username "the-username" :password "the-password"})]
                 (s/get-credentials session) => {:username "the-username" :password "the-password"}))

         (fact "sessions based on the same username and password produce the same credentials"
               (let [session-1 (s/create-session {:username "U" :password "P"})
                     session-2 (s/create-session {:username "U" :password "P"})]
                 (= session-1 session-2) => false
                 (= (s/get-credentials session-1) (s/get-credentials session-2)) => true))

         (fact "session not valid once logged out"
               (let [session (s/create-session {})]
                 (s/valid? session) => true
                 (s/logout session)
                 (s/valid? session) => false)))

  (facts "about session expiry"

         (let [creation-time (t/date-time 2016 12 25 13 15 15)
               expiry-time-one (t/plus creation-time (t/minutes s/max-session-idle-minutes))
               one-second-before-expiry-one (t/minus expiry-time-one (t/seconds 1))
               expiry-time-two (t/plus one-second-before-expiry-one (t/minutes s/max-session-idle-minutes))
               one-second-before-expiry-two (t/minus expiry-time-two (t/seconds 1))]

           (with-redefs [t/now test-clock/now]

             (fact "expired sessions are not available"
                   (test-clock/freeze! creation-time)
                   (let [session (s/create-session {})]
                     (test-clock/freeze! expiry-time-one)
                     (s/get-credentials session) => nil))

             (fact "sessions expire after max-session-idle-minutes"
                   (test-clock/freeze! creation-time)
                   (let [session (s/create-session {})]
                     (s/valid? session) => true
                     (test-clock/freeze! one-second-before-expiry-one)
                     (s/valid? session) => true
                     (test-clock/freeze! expiry-time-one)
                     (s/valid? session) => false))

             (fact "accessing the credentials extends session validity"
                   (test-clock/freeze! creation-time)
                   (let [session (s/create-session {})]
                     (s/valid? session) => true
                     (test-clock/freeze! one-second-before-expiry-one)
                     (s/valid? session) => true
                     (s/get-credentials session) =not=> nil
                     (test-clock/freeze! one-second-before-expiry-two)
                     (s/valid? session) => true
                     (test-clock/freeze! expiry-time-two)
                     (s/valid? session) => false))))))