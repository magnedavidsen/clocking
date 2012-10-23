(ns clocking.db
  (:use korma.db, korma.core))

;; Heroku configures database with environment variable
(def db-url
  (or (System/getenv "DATABASE_URL") "postgres://clocking:clocking@localhost:5432/clocking"))

(defn split-db-url [url]
  "Parses database url from heroku, eg. postgres://user:pass@localhost:1234/db"
  (let [matcher (re-matcher #"^.*://(.*?):(.*?)@(.*?):(\d+)/(.*)$" url)] ;; Setup the regex.
    (when (.find matcher) ;; Check if it matches.
      (zipmap [:match :user :password :host :port :db] (re-groups matcher))))) ;; Construct an options map.


(defdb dev (postgres (split-db-url db-url)))

(defentity employees
  (pk :id)
  (table :employees)
  (entity-fields :id :name)
  (database dev))

(defn get-employee [id]
  (select employees
          (where {:id id})))

(defn create-employee [id, name]
   (insert employees
           (values {:id id, :name name})))

(defn delete-employee [id]
  (delete employees
          (where {:id id})))


(defn list-all-employees []
  (select employees))

(defn update-user [id, name]
   (update employees
           (set-fields {:name name})
           (where {:id id})))

(defentity events
  (pk :id)
  (table :events)
  (entity-fields :id :type :time :employee_id)
  (database dev))

(defn all-events [employee_id]
  (select events (where {:employee_id employee_id})))

(defn most-recent-event [employee_id]
  (select events (where {:employee_id employee_id})))

(defn create-event [type, employee_id]
  (insert events
          (values {:type type, :employee_id employee_id})))

(defn update-event [id, type]
   (update events
           (set-fields {:type type})
           (where {:id id})))

(defn delete-event [id]
  (delete events (where {:id id})))
