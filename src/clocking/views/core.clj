(ns clocking.views.core
  (:require [clocking.views.common :as common]
            [clocking.db :as db]
            [noir.cookies :as cookie]
            [noir.response :as resp])
  (:use compojure.core
        hiccup.form))

(defn index-page []
  (common/layout "index"
                 [:div {:class "form"}
                  (form-to {:autocomplete "off"} [:post "/user/clockin"]
                           [:div {:class "label-input-row"}
                            (label "employee-id" "id: ")
                             (text-field  {:class "employee-id" :maxlength "3" :onchange "setHiddenField()" :autofocus "autofocus" :type "text" :pattern "[0-9]*"} "employee-id")]
                           (submit-button {:class "clock-in"} "clock in"))
                  (form-to [:post "/user/clockout"]
                           (hidden-field "hidden-employee-id")
                           (submit-button {:class "clock-out"} "clock out"))
                  [:p {:class "help-text"}]]))

(defn clockin-page [employee-id]
  (println "test")
  (if
      (empty? (db/get-employee (Integer/parseInt employee-id)))
    (do
      (println "Someone tried clocking in with ID: " employee-id)
      (common/layout
       "index"
       [:p employee-id " is not a registered user."]
       [:meta {:http-equiv "refresh" :content "2;url=/"}]))
    (do
      (db/create-event "clock-in" (Integer/parseInt employee-id))
      (common/layout
       "index"
       [:p employee-id  " has been clocked in."]
       [:meta {:http-equiv "refresh" :content "2;url=/"}]))))

(defn clockout-page [employee-id]
  (if
      (empty? (db/get-employee (Integer/parseInt employee-id)))
    (do
      (println "Someone tried clocking out with ID: " employee-id)
      (common/layout
       "index"
       [:p employee-id " is not a registered user."]
       [:meta {:http-equiv "refresh" :content "2;url=/user"}]))
    (do
      (db/create-event "clock-out" (Integer/parseInt employee-id))
      (common/layout
       "index"
       [:p employee-id " has been clocked out."]
       [:meta {:http-equiv "refresh" :content "2;url=/user"}]))))

(def login-form
  [:div {:class "row"}
   [:div {:class "columns small-12"}
    [:h3 "Login"]
    [:div {:class "row"}
     [:form {:method "POST" :action "login" :class "columns small-4"}
      [:div "Username" [:input {:type "text" :name "username"}]]
      [:div "Password" [:input {:type "password" :name "password"}]]
      [:div [:input {:type "submit" :class "button" :value "Login"}]]]]]])

(defn login-page []
  (common/layout "index"
                 (form-to {:autocomplete "off"} [:post "login"]
                          [:div {:class "label-input-row"}
                           (label "username" "u:")
                           (text-field {:class "passphrase" :size "5" :maxlength "20" :value "user"} "username")
                           (label "password" "p:")
                           (text-field {:class "passphrase" :size "10" :maxlength "20"} "password")]
                          (submit-button {:class "log-in"} "log in"))))

(defroutes handler
  (GET "/" [] (index-page))
  (POST "/clockin" {params :params} (clockin-page (:employee-id params)))
  (POST "/clockout" {params :params} (clockout-page (:hidden-employee-id params)))
  )


