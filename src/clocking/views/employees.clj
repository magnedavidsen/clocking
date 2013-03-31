(ns clocking.views.employees
  (:require [clocking.views.common :as common]
            [clocking.db :as db]
            [clocking.models.events :as events]
            [clocking.models.employees :as employee]
            [noir.cookies :as cookie]
            [noir.response :as resp]
            [cheshire.core :refer :all]
            [noir.fetch.remotes :refer :all]
            [clj-time.coerce :as time]
            [clj-time.core :as time-core]
            )
  (:use [noir.core]
        [hiccup.form]
        [hiccup.page]
        [hiccup.element]))


;;TODO let passphrase be property
(pre-route "/admin/*" {}
           (when-not
               (= "vectra" (cookie/get :passphrase))
             (resp/redirect "/login")))

(pre-route "/_fetch" {}
           (when-not
               (= "vectra" (cookie/get :passphrase))
             (resp/redirect "/login")))


(defpartial cljs-env-aware []
  (if (= (System/getenv "ENVIRONMENT") "dev")
                     [:div
                      [:script {:type "text/javascript" :src "/js/cljs-debug.js"}]
                      [:script {:type "text/javascript"} "goog.require('clocking.client.repl')"]]
                     [:script {:type "text/javascript" :src "/js/cljs.js"}]))

(defpartial add-employee-form []
  (form-to {:autocomplete "off"} [:post "/admin/employees/add"]
           [:span {:class "label-input-row"}
            (label "employee-id" "id: ")
            (text-field {:class "employee-id" :maxlength "3"} "employee-id")]

           [:span {:class "label-input-row"}
            (label "employee-name" "name: ")
            (text-field {:class "employee-name"} "employee-name")]

           (submit-button {:class "add-employee"}  "Add")))

(defpartial employee-row [{:keys [id name]}]
  [:tr
   [:td id] [:td {:class (if (employee/working-now? id) "online icon" "offline icon")} "&#128100;"] [:td name] [:td {:class "timestamp"} (:time (db/most-recent-event id))]
   [:td (link-to (str  "/admin/employees/" id) "Report")]])

(defpartial employees-table [employees]
  [:table
   [:tr
   [:th "ID"] [:th ""] [:th "Name"] [:th "Last event"] [:th ""]]
   (map employee-row employees)])

(defpage "/admin/employees" []
  (common/layout "admin"
   [:h1 "Employees"]
   (add-employee-form)
   (employees-table (db/list-all-employees))))

(defpage "/admin/incomplete" []
  (common/layout-cljs "admin"
                      [:h1 "Incomplete clockings"]
                      [:div {:id "incomplete-app"}]
                      (cljs-env-aware)))

(defpage [:post "/admin/employees/add"] {:as employee}
  (db/create-employee (Integer/parseInt  (:employee-id employee)) (:employee-name employee))
  (render "/admin/employees"))


(defpage "/admin/employees/:id" {:keys [id]}
  (let [id-int (Integer/parseInt id)]
    (common/layout-cljs "admin"
                   [:h1 (:name (first  (db/get-employee id-int)))]
                   [:div {:id "employee-app"}]
                   (cljs-env-aware))))

;todo, writer smarter
(defn convert-date [event]
  {:clock-in (time/to-date (:clock-in event)) :clock-out (time/to-date (:clock-out event)) :date (time/to-date (:date event)) :employee-id (:employee_id event)})

(defremote get-all-events [employee-id]
  (map convert-date
              (events/get-all-events-for-employee employee-id)))

(defremote get-all-incomplete []
  (flatten
   (events/incomplete-days-in-events
    (map #(get-all-events (:id %)) (db/list-all-employees)))))

(defremote save-event [event]
  (let [time (time-core/date-time (:year event) (:month event) (:date event) (:hours event) (:minutes event))]
    (println time)
    (db/save-event {:type (:type event) :employee-id (:employee-id event) :time (time/to-timestamp time)}))
  "OK")
