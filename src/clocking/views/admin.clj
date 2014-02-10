(ns clocking.views.admin
  (:require [clocking.views.common :as common]
            [clocking.db :as db]
            [clocking.models.employees :as employee]
            [noir.cookies :as cookie]
            [noir.response :as resp]
            )
  (:use
   clojure.string
   compojure.core
   hiccup.form
   hiccup.page
   hiccup.element))

(defn parseIntOrEmptyString [number]
  (if (blank? number) 0 (Integer/parseInt number))
  )


(defn cljs-env-aware []
  (if (= (System/getenv "ENV") "dev")
    [:div
     [:script {:type "text/javascript" :src "/js/cljs-debug.js"}]
     [:script {:type "text/javascript"} "goog.require('clocking.client.repl')"]]
    [:script {:type "text/javascript" :src "/js/cljs.js"}]))

(defn add-employee-form []
  (form-to {:autocomplete "off"} [:post "/admin/employees/add"]
           [:span {:class "label-input-row"}
            (label "employee-id" "id: ")
            (text-field {:class "employee-id" :maxlength "3"} "employee-id")

            (label "norpost-id" "norpost id: ")
            (text-field {:class "employee-id"} "norpost-id" (:norpost_id user))

            (label "employee-name" "name: ")
            (text-field {:class "employee-name"} "employee-name")]

           (submit-button {:class "add-employee"}  "Add")))

(defn update-employee-form [user-id]
  (form-to {:autocomplete "off"} [:put (str "/admin/employees/" user-id)]
           (let [user (first (db/get-employee (Integer/parseInt user-id)))]

             [:span {:class "label-input-row"}
              (label "employee-id" "id: ")
              (text-field {:class "employee-id" :maxlength "3" :readonly "true"} "employee-id" user-id)

              (label "norpost-id" "norpost id: ")
              (text-field {:class "employee-id"} "norpost-id" (:norpost_id user))

              (label "employee-name" "name: ")
              (text-field {:class "employee-name"} "employee-name" (:name user))])

           (submit-button {:class "add-employee"}  "Save")))


(defn employee-row [{:keys [id name norpost_id]}]
  [:tr
   [:td id] [:td norpost_id] [:td {:class (if (employee/working-now? id) "online icon" "offline icon")} "&#128100;"] [:td name] [:td {:class "timestamp"} (:time (db/most-recent-event id))]
   [:td (link-to (str  "/admin/employees/" id) "Report")] [:td (link-to (str  "/admin/employees/" id "/edit") "Edit")]])

(defn employees-table [employees]
  [:table
   [:tr
    [:th "ID"] [:th "Norpost ID"] [:th ""] [:th "Name"] [:th "Last event"] [:th ""] [:th ""]]
   (map employee-row employees)])

(defn admin-page []
  (common/layout "admin"
                 [:h1 "Admin"]
                 [:p (link-to (str  "/admin/employees") "Employees")]
                 [:p (link-to (str  "/admin/incomplete") "Incomplete clockings")]))

(defn employees-page []
  (common/layout "admin"
                 [:h1 "Employees"]
                 (add-employee-form)
                 (employees-table (db/list-all-employees))))

(defn edit-employee-page [user-id]
  (common/layout "admin"
                 [:h1 "Employees"]
                 (update-employee-form user-id)
                 (employees-table (db/list-all-employees))))

(defn incomplete-page []
  (common/layout-cljs "admin"
                      [:h1 "Incomplete clockings"]
                      [:div {:id "incomplete-app"}]
                      (cljs-env-aware)))

(defn add-employee [employee]
  (db/create-employee (Integer/parseInt  (:employee-id employee)) (:employee-name employee) (Integer/parseInt  (:norpost-id employee)))
  (employees-page))

(defn update-employee [employee]
  (db/update-user (Integer/parseInt  (:employee-id employee)) (:employee-name employee) (parseIntOrEmptyString (:norpost-id employee)))
  (employees-page))

(defn employee-page [id]
  (let [id-int (Integer/parseInt id)]
    (common/layout-cljs "admin"
                        [:h1 (:name (first  (db/get-employee id-int)))]
                        [:a {:href (str "/api/event/" id "/report.csv")} "Download report"]
                        [:div {:id "employee-app"}]
                        (cljs-env-aware))))

(defroutes handler
  (GET "/"[] (admin-page))
  (GET "/employees" [] (employees-page))
  (GET "/employees/:id" [id] (employee-page id))
  (PUT "/employees/:id" {params :params} (update-employee params))
  (GET "/employees/:id/edit" [id] (edit-employee-page id))
  (POST "/employees/add" {params :params} (add-employee params))
  (GET "/incomplete" [] (incomplete-page))
  )
