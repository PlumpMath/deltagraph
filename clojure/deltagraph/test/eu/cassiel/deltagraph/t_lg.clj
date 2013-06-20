(ns eu.cassiel.deltagraph.t-lg
  (:use midje.sweet
        clojure.test
        slingshot.test)
  (:require (eu.cassiel.deltagraph [lg :as lg])))

(fact "orphan-edges"
      (lg/put-edge lg/empty-graph (lg/new-edge (lg/new-vertex) (lg/new-vertex)))
      => (throws clojure.lang.ExceptionInfo #":type :.*/VERTEX-NOT-PRESENT"))

(fact "vertex add"
      (let [v1 (lg/new-vertex)
            v2 (lg/new-vertex)
            e (lg/new-edge v1 v2)
            g (-> lg/empty-graph
                  (lg/put-vertex v1)
                  (lg/put-vertex v2)
                  (lg/put-edge e))]
        (count (lg/vertices g)) => 2
        (count (lg/edges g)) => 1))
