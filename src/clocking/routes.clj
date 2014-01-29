(ns clocking.routes
  (use '[compojure.core :as compojure :only (GET ANY defroutes)]))

(defroutes user-routes
  (GET "/account" request (page-bodies (:uri request)))
  (GET "/private-page" request (page-bodies (:uri request))))

(defroutes admin-routes

  )

(defroutes ring-app
  ;; requires user role
  (compojure/context "/" request
    (friend/wrap-authorize user-routes #{::user}))

  ;; requires admin role
  (compojure/context "/admin" request
    (friend/wrap-authorize admin-routes #{::admin}))

  ;; anonymous
  (GET "/" request "Landing page.")
  (GET "/login" request "Login page.")
  (friend/logout (ANY "/logout" request (ring.util.response/redirect "/"))))