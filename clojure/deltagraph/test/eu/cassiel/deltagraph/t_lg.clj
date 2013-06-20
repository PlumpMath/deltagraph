(ns eu.cassiel.deltagraph.t-lg
  (:use midje.sweet
        clojure.test
        slingshot.test)
  (:require (eu.cassiel.deltagraph [lg :as lg])))

(deftest orphan-edges
  (is (thrown+? [:type ::lg/VERTEX-NOT-PRESENT]
                (lg/put-edge lg/empty-graph (lg/new-edge (lg/new-vertex) (lg/new-vertex))))))
