(ns clocking.migration
  (:require [clojure.java.jdbc :as sql]
            [clocking.db :as db]))

(def subname
  (str "//" (db/get-host db/db-url) ":" (db/get-port db/db-url) "/" (db/get-db db/db-url)))

(def db
     {:classname "org.postgresql.Driver"
      :subprotocol "postgres"
      :subname subname})

(defn create-employees []
  (sql/with-connection db
    (sql/create-table :employees
                      [:id :integer "PRIMARY KEY"]
                      [:name :text "NOT NULL"])))

(defn create-events []
  (sql/with-connection db
    (sql/create-table :events
                      [:id :serial "PRIMARY KEY"]
                      [:type :text]
                      [:time :timestamp "DEFAULT CURRENT_TIMESTAMP"])))

(defn -main []
    (print "Migrating database...") (flush)
    (create-employees)
    (create-events)
    (println " done"))
