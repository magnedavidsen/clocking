(ns clocking.server
  (:use compojure.core
        [ring.adapter.jetty :only (run-jetty)]
        [ring.middleware.reload :only (wrap-reload)]
        [ring.middleware.edn :only (wrap-edn-params)]
        [hiccup.middleware :only (wrap-base-url)]
        [clocking.middleware :only (wrap-request-logging)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            clocking.views.core
            clocking.views.employees
            clocking.views.api
            [cemerick.friend :refer (authenticate wrap-authorize)]
            [cemerick.friend.credentials :refer (hash-bcrypt bcrypt-credential-fn)]
            [cemerick.friend.workflows :refer (interactive-form)]

            ))


(def users {"root" {:username "root"
                    :password (hash-bcrypt "admin_password")
                    :roles #{::admin}}
            "jane" {:username "jane"
                    :password (hash-bcrypt "user_password")
                    :roles #{::user}}})


(defroutes main-routes
  (context "/" request
    (wrap-authorize clocking.views.core/handler #{::user}))


  clocking.views.core/handler
  clocking.views.employees/handler
  clocking.views.api/handler
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (authenticate {:credential-fn (partial bcrypt-credential-fn users)
                          :workflows [(interactive-form)]})
      (wrap-base-url)
      (wrap-edn-params)
      (wrap-request-logging)
      (wrap-reload '(clocking.server))))

;; convenience-method for repl use etc
(defn run-server [port]
  (defonce server (run-jetty #'app {:port port :join? false})))

(defn -main [port]
  (run-server (if (nil? port) 8080 (Integer/parseInt port))))