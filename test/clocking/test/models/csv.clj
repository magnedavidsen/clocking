(ns clocking.test.models.csv
  (:use [clojure.test]
        [clocking.models.csv :as csv]
        [clj-time.coerce]))

(def list-of-flattened-events
  [{:employee_id 100, :date (from-sql-date #inst "2012-10-16T23:49:07.818-00:00"), :clock-in (from-sql-date #inst  "2012-10-16T23:49:07.818-00:00")}
   {:employee_id 100, :date (from-sql-date #inst "2012-10-16T23:48:07.818-00:00"),  :clock-in (from-sql-date  #inst "2012-10-16T23:48:07.818-00:00"), :clock-out (from-sql-date #inst "2012-10-16T23:49:07.818-00:00")}
   {:employee_id 100, :date (from-sql-date #inst "2012-10-16T23:47:07.818-00:00"), :clock-out (from-sql-date #inst  "2012-10-16T23:49:07.818-00:00")}])

(def wanted-csv
  "Employee id;Date;Clocked in;Clocked out;Minutes worked\n100;17.10.2012;00:49;;\n100;17.10.2012;00:48;00:49;1\n100;17.10.2012;;00:49;\n")

(deftest csv-generated-correctly
  (is (= wanted-csv
         (csv/generate-csv list-of-flattened-events)
         )))
