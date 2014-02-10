(ns clocking.views.status

  (:use

   compojure.core))

(defroutes
  (GET "/status/timezone" (str (time/default-time-zone))))
