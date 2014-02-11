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
            clocking.views.admin
            clocking.views.api
            clocking.views.status
            [hiccup.page :as h]
            [ring.util.response :as resp]
            [cemerick.friend :as friend]
            [cemerick.friend :refer (authenticate wrap-authorize logout*)]
            [cemerick.friend.credentials :refer (hash-bcrypt bcrypt-credential-fn)]
            [cemerick.friend.workflows :refer (interactive-form)]))

(def users {"admin" {:username "admin"
                     :password (hash-bcrypt (or (System/getenv "ADMIN_PASS") "admin"))
                     :roles #{::admin}}
            "user" {:username "user"
                    :password (hash-bcrypt (or (System/getenv "USER_PASS") "user"))
                    :roles #{::user}}})

(derive ::admin ::user)

(defroutes user-routes
  clocking.views.core/handler)

(defroutes main-routes
  clocking.views.status/handler
  (context "/user" request
           (wrap-authorize user-routes  #{::user}))

  (context "/admin" request
           (wrap-authorize clocking.views.admin/handler  #{::admin}))
  (context "/api" request
           (wrap-authorize clocking.views.api/handler  #{::admin}))
  (GET "/" request (resp/redirect (str (:context request) "/user")))
  (GET "/login" request (clocking.views.core/login-page))
  (GET "/logout" request (logout* (resp/redirect (str (:context request) "/"))))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> main-routes
      (authenticate
       {:credential-fn (partial bcrypt-credential-fn users)
        :unauthorized-handler #(if-let [msg (-> % ::friend/authorization-failure :response-msg)]
                                 {:status 403 :body msg}
                                 (#'friend/default-unauthorized-handler %))
        :workflows [(interactive-form)]})
      (wrap-edn-params)
      (wrap-base-url)
      (wrap-request-logging)
      (wrap-reload '(clocking.server))
      (handler/site)))

(defn run-server [port]
  (defonce server (run-jetty #'app {:port port :join? false})))

;; convenience-method for repl use etc
(defn -main [port]
  (run-server (if (nil? port) 8080 (Integer/parseInt port))))
