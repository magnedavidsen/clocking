(ns clocking.server
  (:use compojure.core
        [ring.adapter.jetty :only (run-jetty)]
        [ring.middleware.reload :only (wrap-reload)]
        [ring.middleware.edn :only (wrap-edn-params)]
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            clocking.views.core
            clocking.views.employees
            clocking.views.api
            ))

(defroutes main-routes
  clocking.views.core/handler
  clocking.views.employees/handler
  clocking.views.api/handler
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)
      (wrap-edn-params)
      (wrap-reload '(clocking.server))))

;; convenience-method for repl use etc
(defn run-server [port]
  (defonce server (run-jetty #'app {:port port :join? false})))

(defn -main [port]
  (run-server (if (nil? port) 8080 (Integer/parseInt port))))