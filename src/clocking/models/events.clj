(ns clocking.models.events
  (:require [clocking.db :as db]
            [clj-time.core :as time])
  (:use
   [clj-time.coerce]))

(defn same-day? [first-event second-event]
  (let [first-date (:time first-event) second-date (:time second-event)]
    (= (time/day first-date) (time/day second-date))))

(defn proper-pair? [first-event second-event]
  (if (nil? second-event)
    false
    (and (and (same-day? first-event second-event) (= "clock-in" (:type first-event))) (= "clock-out" (:type second-event)))))


(defn pair-clockins-and-clockouts [events paired-events]
  (if (empty? events)
    paired-events
    (if (proper-pair? (first events) (second events))
      ; pair two events, and continue
      (pair-clockins-and-clockouts (rest (rest events)) (conj paired-events (vector (first events) (second events))))
      ; "pair" one event, and continue
      (pair-clockins-and-clockouts (rest events)
                                   (conj paired-events (vector (first events)))))))

(defn flatten-type-and-time [event]
  {(keyword (:type event)) (:time event)})

(defn flatten-two-events [events]
  (let [flat-type-time (map flatten-type-and-time events)]
    (merge
     {:employee_id (:employee_id (first events)) :date (:time (first events)) }
     (merge (first flat-type-time) (second flat-type-time)))))

(defn get-all-events-for-employee [id]
  (map flatten-two-events (pair-clockins-and-clockouts
                           (db/all-events id) [])))

(defn time-between-timestamps [timestamp-one timestamp-two]
  (if (not-any? nil? [timestamp-one timestamp-two])
    (time/in-minutes (time/interval  timestamp-one))))
