(ns clocking.test.db
  (:require [clocking.db :as db])
  (:use [clojure.test]))

(deftest addition
  (is (= 4 (+ 2 2)))
  (is (= 7 (+ 3 4))))

(deftest subtraction
  (is (= 1 (- 4 3)))
  (is (= 3 (- 7 4))))

(def db-url "postgres://user:pass@localhost:1234/db")
(def db-url-without-user "postgres://:@localhost:1234/db")


(deftest url-dbconf
  (is (= "db" (:db (db/split-db-url db-url))))
  (is (= "user" (:user (db/split-db-url db-url))))
  (is (= "pass" (:password (db/split-db-url db-url))))
  (is (= "localhost" (:host (db/split-db-url db-url))))
  (is (= "1234" (:port (db/split-db-url db-url)))))

(deftest url-dbconf-without-user
  (is (= "db" (:db (db/split-db-url db-url-without-user))))
  (is (= "" (:user (db/split-db-url db-url-without-user))))
  (is (= "" (:password (db/split-db-url db-url-without-user))))
  (is (= "localhost" (:host (db/split-db-url db-url-without-user))))
  (is (= "1234" (:port (db/split-db-url db-url-without-user)))))
