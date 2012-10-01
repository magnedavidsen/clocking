(ns tidsur.test.core
    (:use [tidsur.views.core])
    (:use [clojure.test]))

(deftest it-says-tidsur 
    (let [req {}
          resp (app req)]
    (is (= 200 (:status resp)))
    (is (= "Tidsur" (:body resp)))))