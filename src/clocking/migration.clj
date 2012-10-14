(ns clocking.migration
  (:require [clojure.java.jdbc :as sql]
            [clocking.db :as db]))

(def subname
  (str "//" (:host (db/split-db-url db/db-url)) ":" (:port (db/split-db-url db/db-url)) "/" (:db (db/split-db-url db/db-url))))

(def db {:classname "org.postgresql.Driver"
         :subprotocol "postgresql"
         :subname subname
         :user (:user (db/split-db-url db/db-url))
         :password (:pass (db/split-db-url db/db-url))})

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
