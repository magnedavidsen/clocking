(ns clocking.client.employees
  (:require [fetch.remotes :as remotes]
            [goog.dom :as googdom]
            [goog.ui.DatePicker]
            [clojure.browser.dom :as dom]
            [dommy.template :as template])
  (:require-macros [fetch.macros :as fm]))

(def datepicker (new goog.ui.DatePicker))

(def userid 100)

(defn page []
  (template/node
   [:div {:id "wrapper"}
    (employee-report all-events)]))

(defn buildpage []
  (dom/replace-node (googdom/getElement "wrapper") (page)))

(defn minutes-between [clock-in clock-out]
  (when (and clock-in clock-out) (.minBetween js/Date clock-in clock-out)))

(defn event-row [{:keys [date clock-in clock-out]}]
  (template/node
   [:tr
    [:td (js/formatDate (str date))]
    [:td (js/formatTime (str clock-in))]
    [:td (js/formatTime (str clock-out))]
    [:td (js/formatMinutes (minutes-between clock-in clock-out))]]))

(defn employee-report [events]
  (template/node
   [:table
    [:tr
     [:th "Date"] [:th "Clocked in"] [:th "Clocked out" ] [:th "Sum"]]
    (map event-row events)]))

(defn populate-report [events]
  (dom/append wrapper (employee-report events)))

;;todo - defonce doesn't work. do we have state now?
(defn get-events-from-server []
  (fm/letrem [events (get-all-events userid)]
             (def all-events events)))

(get-events-from-server)
(populate-report all-events)

(defn date-bigger-than [event]
  (< (.getTime (:date event)) (.getTime (new js/Date 2013, 11, 3))))

(filter date-bigger-than all-events)

(.getTime (:date (first all-events)))

(buildpage)
