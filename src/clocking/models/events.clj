(ns clocking.models.events
  (:require [clocking.db :as db]
            [clj-time.core :as time]
            [clj-time.format :as f]))

(def custom-formatter (f/formatter "yyyyMMdd"))

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
  {(keyword (:type event)) (:time event) (keyword (str (:type event) "-id")) (:id event)})

(defn flatten-two-events [events]
  (let [flat-type-time (map flatten-type-and-time events)]
    (merge
     {:employee_id (:employee_id (first events)) :date (:time (first events)) }
     (merge (first flat-type-time) (second flat-type-time)))))

(defn get-all-events-for-employee [id]
  (map flatten-two-events (pair-clockins-and-clockouts
                           (db/all-events id) [])))

(defn get-all-events-for-employee-in-interval [id from to]

  (def to-plus-one
    (time/plus (f/parse custom-formatter to) (time/days 1)))

  (defn within-interval [event]
    (time/within? (time/interval (f/parse custom-formatter from) to-plus-one)
              (:time event)))

  (map flatten-two-events (pair-clockins-and-clockouts
                            (filter within-interval
                             (db/all-events id)) [])))

(defn incomplete-days-in-events [paired-events]
  (filter #(or (nil? (:clock-out %)) (nil? (:clock-in %))) paired-events))
