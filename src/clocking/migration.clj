(ns clocking.migration
  (:require [clojure.java.jdbc :as sql]))

(defn create-employees []
  (sql/with-connection (System/getenv "DATABASE_URL")
    (sql/create-table :employees
                      [:id :integer "PRIMARY KEY"]
                      [:name :text "NOT NULL"])
    (sql/create-table :events
                      [:id :serial "PRIMARY KEY"]
                      [:type :text]
                      [:time :timestamp ])
    ))

(defn -main []
  (print "Migrating database...") (flush)
  (create-shouts)
  (println " done"))
