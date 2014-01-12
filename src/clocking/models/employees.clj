(ns clocking.models.employees
  (:require [clocking.db :as db]))

(defn working-now? [employee-id]
  (= "clock-in" (:type (db/most-recent-event employee-id))))