(ns clocking.models.csv
  (:use [clojure-csv.core])
  (:require
        [clocking.db :as db]
        [clocking.models.events :as events]
   [clj-time.format :as timeformat]
   [clj-time.core :as time]))

(def time-formatter (timeformat/formatter "HH:mm"))
(def date-formatter (timeformat/formatter "dd.MM.yyyy"))

(defn to-csv-timestamp-with-formatter [timestamp, formatter]
  (if (nil? timestamp) ""
    (timeformat/unparse formatter (time/from-time-zone timestamp (time/time-zone-for-offset -1)))))

(defn interval-in-minutes [clock-in clock-out]
  (if (or (nil? clock-in) (nil? clock-out)) ""
    (if (time/before? clock-out clock-in) ""
      (time/in-minutes (time/interval clock-in clock-out)))))

(defn map-to-string-seq-seq [events]
  (defn stringify-event [event]
    (let [new-map [(:employee_id event)  (to-csv-timestamp-with-formatter (:date event) date-formatter) (to-csv-timestamp-with-formatter (:clock-in event) time-formatter)
                   (to-csv-timestamp-with-formatter (:clock-out event) time-formatter) (interval-in-minutes (:clock-in event) (:clock-out event))]]
      (map #(str "" % "") new-map)
      ))
  (map stringify-event events))

(defn generate-csv [events]
  (str
  (write-csv [["Employee id" "Date" "Clocked in" "Clocked out" "Minutes worked"]] :delimiter ";")
  (write-csv (map-to-string-seq-seq events) :delimiter ";")))
