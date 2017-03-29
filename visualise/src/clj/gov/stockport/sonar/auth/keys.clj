(ns gov.stockport.sonar.auth.keys
  (:import (java.security KeyPairGenerator KeyPair)))

(def ^KeyPairGenerator key-pair-generator
  (doto
    ; RSA / [B]ouncy [C]astle Provider
    (KeyPairGenerator/getInstance "RSA" "BC")
    (.initialize 2048)))

(defonce ^KeyPair key-pair (.generateKeyPair key-pair-generator))

(defonce privkey (.getPrivate key-pair))
(defonce pubkey (.getPublic key-pair))

