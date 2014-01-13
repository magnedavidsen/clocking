(ns clocking.views.core
  (:require [clocking.views.common :as common]
            [noir.content.getting-started]
            [noir.validation :as vali]
            [clocking.db :as db]
            [noir.cookies :as cookie]
            [noir.response :as resp])
  (:use compojure.core
        hiccup.form))

;;TODO security needs to be reimplemented
;;(pre-route [:get ["/:path" :path #"(?!login|logout)*"]]  {}
;;          (when-not
;;               (or
;;                (= "stemplingsur" (cookie/get :passphrase))
;;                (= "vectra" (cookie/get :passphrase)))
;;             (resp/redirect "/login")))

(defn index-page []
  (common/layout "index"
                 [:div {:class "form"}
                  (form-to {:autocomplete "off"} [:post "/clockin"]
                           [:div {:class "label-input-row"}
                            (label "employee-id" "id: ")
                             (text-field  {:class "employee-id" :maxlength "3" :onchange "setHiddenField()" :autofocus "autofocus" :type "text" :pattern "[0-9]*"} "employee-id")]
                           (submit-button {:class "clock-in"} "clock in"))
                  (form-to [:post "/clockout"]
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
       [:meta {:http-equiv "refresh" :content "2;url=/"}]))
    (do
      (db/create-event "clock-out" (Integer/parseInt employee-id))
      (common/layout
       "index"
       [:p employee-id " has been clocked out."]
       [:meta {:http-equiv "refresh" :content "2;url=/"}]))))

(defn login-page []
  (common/layout "index"
                 (form-to {:autocomplete "off"} [:post "/login"]
                          [:div {:class "label-input-row"}
                           (text-field {:class "passphrase" :size "10" :maxlength "20"} "passphrase")]
                          (submit-button {:class "log-in"} "log in"))))

(defn login [passphrase]
  (cookie/put! :passphrase passphrase)
  (if (= "vectra" passphrase)
    (resp/redirect "/admin/employees")
    (resp/redirect "/")))

(defn logout []
  (cookie/put! :passphrase "")
  (resp/redirect "/"))

(defroutes handler
  (GET "/" [] (index-page))
  (GET "/login" [] (login-page))
  (POST "/login" {params :params} (login (params :passphrase)))
  (GET "/logout"[] (logout))
  (POST "/clockin" {params :params} (clockin-page (params :employee-id)))
  (POST "/clockout" {params :params} (clockout-page (params :hidden-employee-id)))
  )


