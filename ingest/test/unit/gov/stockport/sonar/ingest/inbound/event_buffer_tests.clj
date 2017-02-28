(ns gov.stockport.sonar.ingest.inbound.event-buffer-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.inbound.event-buffer :as buffer]))

(def !flushed (atom nil))

(defn flusher [{:keys [events]}]
  (reset! !flushed events))

(against-background
  [(before :facts (reset! !flushed nil))]

  (fact "it buffers events without flushing if not full"
        (let [{:keys [queue]} (buffer/create-buffer {:capacity 2 :flush-fn flusher :feed-hash ..feed-hash..})]
          (queue ..event-one..) => {:capacity 2 :qty 1 :events [..event-one..] :feed-hash ..feed-hash..}
          @!flushed => nil))

  (fact "it buffers events, flushes, and resets when full"
        (let [{:keys [queue]} (buffer/create-buffer {:capacity 2 :flush-fn flusher :feed-hash ..feed-hash..})]
          (queue ..event-one..) => {:capacity 2 :qty 1 :feed-hash ..feed-hash.. :events [..event-one..]}
          (queue ..event-two..) => {:capacity 2 :qty 2 :feed-hash ..feed-hash.. :events [..event-one.. ..event-two..]}
          @!flushed => [..event-one.. ..event-two..]
          (queue ..event-three..) => {:capacity 2 :qty 1 :feed-hash ..feed-hash.. :events [..event-three..]}
          @!flushed => [..event-one.. ..event-two..]))

  (fact "it flushes on demand"
        (let [{:keys [queue flush]} (buffer/create-buffer {:capacity 2 :flush-fn flusher :feed-hash ..feed-hash..})]
          (queue ..event-one..) => {:capacity 2 :qty 1 :feed-hash ..feed-hash.. :events [..event-one..]}
          @!flushed => nil
          (flush) => {:capacity 2 :qty 0 :feed-hash ..feed-hash.. :events []}
          @!flushed => [..event-one..])))