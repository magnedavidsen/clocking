(ns clocking.db
  (:use korma.db, korma.core))

;; TODO: Use existing Java-class for this stuff
(defn split-url [url]
  (re-seq #"[^:\/]+" url))

(defn get-host [url]
  (first (rest (split-url url))))

(defn get-port [url]
  (first (rest (rest (split-url url)))))

(defn get-db [url]
  (first (rest (rest (rest (split-url url))))))

;; Heroku configures database with environment variable
(def database-url (System/getenv "DATABASE_URL")

(if (nil? database-url)
  (if (not (nil? database-url))
    (defdb dev (postgres {:db (get-db database-url)
                          :host (get-host database-url)
                          :port (get-port database-url)}))
    ; assume there's a local database if database url is nil
    (defdb dev (postgres {:user "clocking"
                          :password "clocking"
                          :db "clocking"
                          :host "localhost"
                          :port "5432"}))))
                 
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

(defn all-events [id]
  (select events (where {:id id})))

(defn create-event [id, type, employee_id]
  (insert events
          (values {:id id, :type type, :employee_id employee_id})))

(defn update-event [id, type]
   (update events
           (set-fields {:type type})
           (where {:id id})))

(defn delete-event [id]
  (delete events (where {:id id})))
