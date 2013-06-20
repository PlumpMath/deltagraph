(ns eu.cassiel.deltagraph.t-lg
  (:use midje.sweet
        clojure.test
        slingshot.test)
  (:require (eu.cassiel.deltagraph [lg :as lg]))
  (:import [clojure.lang ExceptionInfo]))

(fact "orphan-edges"
      (lg/put-edge lg/empty-graph (lg/new-edge (lg/new-vertex) (lg/new-vertex)))
      => (throws ExceptionInfo #":type :.*/VERTEX-NOT-PRESENT"))

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

(facts "other"
       (fact "vertex other"
             (let [v1 (lg/new-vertex)
                   v2 (lg/new-vertex)
                   e (lg/new-edge v1 v2)
                   g (-> lg/empty-graph
                         (lg/put-vertex v1)
                         (lg/put-vertex v2)
                         (lg/put-edge e))]
               (:id (lg/other g e v1)) => (:id v2)
               (:id (lg/other g e v2)) => (:id v1)))

       (fact "vertex not in edge"
             (let [[v1 v2 v3] (repeatedly 3 lg/new-vertex)
                   e (lg/new-edge v1 v2)
                   g (-> lg/empty-graph
                         (lg/put-vertex v1)
                         (lg/put-vertex v2)
                         (lg/put-vertex v3))]
               (lg/other g e v3)
               => (throws ExceptionInfo #":type :.*/VERTEX-NOT-IN-EDGE"))))
