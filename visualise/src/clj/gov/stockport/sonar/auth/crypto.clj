(ns gov.stockport.sonar.auth.crypto
  (:require [buddy.core.crypto :as crypto]
            [buddy.core.codecs :as codecs]
            [buddy.core.nonce :as nonce]
            [buddy.core.hash :as hash]))

(def iv (nonce/random-bytes 16))

(def algo {:algorithm :aes128-cbc-hmac-sha256})

(defn- fixed-length-password [n]
   (let [chars (map char (range 33 127))
         password (take n (repeatedly #(rand-nth chars)))]
     (reduce str password)))

(def secret-key-hasher hash/sha3-256)

(defn encrypt [content]
  (let [secret-key (fixed-length-password 32)]
    {:secret-key secret-key
     :ciphertext (crypto/encrypt
                   (codecs/to-bytes content)
                   (secret-key-hasher secret-key)
                   iv
                   algo)}))

(defn decrypt [secret-key ciphertext]
  (codecs/bytes->str (crypto/decrypt ciphertext (secret-key-hasher secret-key) iv algo)))
