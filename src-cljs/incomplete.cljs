(ns clocking.client.incomplete
  (:require [clocking.client.common :as common]
            [fetch.remotes :as remotes]
            [goog.dom :as googdom]
            [goog.events :as events]
            [goog.i18n.DateTimeFormat]
            [goog.date.DateTime]
            [goog.date.Date]
            [clojure.browser.dom :as dom]
            [dommy.template :as template]
            )
  (:require-macros [fetch.macros :as fm]))

(defn valid-time? [time-array]
  "Expects first to be hours, and second to be minutes"
  (.log js/console time-array)
  (when (nil? (first time-array)) false)
  (when (nil? (second time-array)) false)
  (let [hours (js/parseInt (first time-array)) minutes (js/parseInt (second time-array))]
    (and (<= 0 hours 23) (<= 0 minutes 59))))

(defn save-new-event [event]
  (validate-time ())
  (.log js/console (+ "Sending object to backend: " (str event)))
  (fm/remote (save-event event) [result] (js/alert result)))

(defn new-datetime [{:keys [year month date hours minutes]}]
  (let [datetime (new goog.date.DateTime year month date hours minutes)]
    (.toUTCIsoString datetime false true)))

(defn new-event-component [{:keys [type employee-id date]}]
  (let [submit-button (template/node [:button {:class "submit"}])]
    (let [input-field (template/node [:input])]
      (defn click-handler []
        (let [time-array (clojure.string/split (.-value input-field) #":")]
          (if (valid-time? time-array)
            (save-new-event {:type type :employee-id employee-id
                             :time  (new-datetime {:year (.getYear date) :month (.getMonth date) :date (.getDate date) :hours (js/parseInt (first time-array)) :minutes (js/parseInt (second time-array))})})
            (js/alert "Time is not in the right format (HH:mm)")
            )))
      (events/listen submit-button goog.events.EventType.CLICK click-handler)
      (template/node
       [:div
        input-field submit-button]))))

(defn event-row [{:keys [employee-id date clock-in clock-out]}]
  (template/node
   [:tr
    [:td (when-not (nil? employee-id) employee-id)]
    [:td (when-not (nil? date) (.format common/date-formatter date))]
    [:td (if (nil? clock-in)
           (new-event-component {:type "clock-in" :employee-id employee-id :date date})
           (.format common/time-formatter clock-in))]
    [:td (if (nil? clock-out)
           (new-event-component {:type "clock-out" :employee-id employee-id :date date})
           (.format common/time-formatter clock-out))]]))

(defn incomplete-report [events]
  (template/node
   [:div {:class "incomplete-report"}
    [:table [:tr [:th "Employee id"] [:th "Date"] [:th "Clocked in"] [:th "Clocked out"]]
     (map event-row events)]]))

(defn start-page []
  (template/node
   [:div {:id "incomplete-app"}
    (incomplete-report all-events)]))

(defn buildpage []
  (.log js/console "Starting to build page.")
  (dom/replace-node (googdom/getElement "incomplete-app") (start-page)))

;;TODO is it ok to do def all-events here?
(defn get-events-from-server []
  (.log js/console "Getting events from server.")
  (fm/letrem [events (get-all-incomplete)]
             (.log js/console "Events returned")
             (def all-events (map common/convert-date-to-goog events))
             (buildpage)))

;;TODO find better way to start different apps
(when (not (nil? (googdom/getElement "incomplete-app") )) (get-events-from-server))
