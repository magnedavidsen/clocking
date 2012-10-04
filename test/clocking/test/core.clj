(ns clocking.test.core
    (:use [clocking.views.core])
    (:use [clojure.test]))

(deftest it-says-clocking 
    (let [req {}
          resp (app req)]
    (is (= 200 (:status resp)))
    (is (= "clocking" (:body resp)))))