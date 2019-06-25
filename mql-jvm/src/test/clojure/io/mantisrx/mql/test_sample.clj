(ns io.mantisrx.mql.test-sample
  ""
  (:require [clojure.test :refer :all]
            [io.mantisrx.mql.jvm.core :as mql]
            [io.mantisrx.mql.jvm.interfaces.server :as mql-server]
            [io.mantisrx.mql.jvm.interfaces.core :as mqli]
            [rx.lang.clojure.core :as rx]
            [rx.lang.clojure.blocking :as rxb])
  (:import rx.Observable java.util.concurrent.TimeUnit))

(deftest test-random-sampler
  (testing ""
    (let
      [q "select * from stream sample {'strategy': 'RANDOM', 'threshold': 1}"
       n 100000
       percent (/ 1 10000)
       data (repeat n {"a" 1})
       context {"stream" (Observable/from data)}
       result (rxb/into [] (mql/eval-mql q context))
       ]
      (is (> (* (/ 2 10000) n) (count result)))
      (is (< (* (/ 0 10000) n) (count result))))))

(deftest test-server-sampling
  (testing ""
    (let
      [q "select * from stream sample {'strategy': 'RANDOM', 'threshold': 1}"
       n 100000
       percent (/ 1 10000)
       data (repeat n {"a" 1})
       sampler (mqli/query->sampler q)
       result (filter sampler data)
       ]
      (is (> (* (/ 2 10000) n) (count result)))
      (is (< (* (/ 0 10000) n) (count result))))))

(comment (deftest test-query-sampling
  (testing ""
    (let
      [q "select * from stream sample {'strategy': 'RANDOM', 'threshold': 1}"
       query (mql-server/make-query "test" q)
       n 100000
       percent (/ 1 10000)
       data (repeat n {"a" 1})
       result (filter #(.matches query %) data)
       ]
      (is (> (* (/ 2 10000) n) (count result)))
      (is (< (* (/ 0 10000) n) (count result)))))))
