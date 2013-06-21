(ns eu.cassiel.deltagraph.t-lg
  (:use midje.sweet
        clojure.test
        slingshot.test)
  (:require (eu.cassiel.deltagraph [lg :as lg]))
  (:import [clojure.lang ExceptionInfo]))

(fact "orphan-edges"
      (let [g lg/empty-graph
            [g v1] (lg/add-vertex g)
            [g v2] (lg/add-vertex g)]
        (lg/add-edge lg/empty-graph v1 v2)
        => (throws ExceptionInfo #":type :.*/VERTEX-NOT-PRESENT")))

(fact "vertex add"
      (let [g lg/empty-graph
            [g v1] (lg/add-vertex g)
            [g v2] (lg/add-vertex g)
            [g e] (lg/add-edge g v1 v2)]
        (count (lg/vertices g)) => 2
        (count (lg/edges g)) => 1))

(facts "other"
       (fact "vertex other"
             (let [g lg/empty-graph
                   [g v1] (lg/add-vertex g)
                   [g v2] (lg/add-vertex g)
                   [g e] (lg/add-edge g v1 v2)]
               (:id (lg/other g e v1)) => (:id v2)
               (:id (lg/other g e v2)) => (:id v1)))

       (fact "vertex not in edge"
             (let [[g v1] (lg/add-vertex lg/empty-graph)
                   [g v2] (lg/add-vertex g)
                   [g v3] (lg/add-vertex g)
                   [g e] (lg/add-edge g v1 v2)]
               (lg/other g e v3)
               => (throws ExceptionInfo #":type :.*/VERTEX-NOT-IN-EDGE"))))

(facts "retrieval"
       (fact "retrieve vertex"
             (let [[g v] (lg/add-vertex lg/empty-graph)]
               (lg/retrieve-vertex g (lg/add-vertex lg/empty-graph))
               => (throws ExceptionInfo #":type :.*/VERTEX-NOT-IN-GRAPH")

               (:id (lg/retrieve-vertex g v)) => (:id v)))

       (fact "retrieve edge"
             (let [[g v1] (lg/add-vertex lg/empty-graph)
                   [g v2] (lg/add-vertex g)
                   [g e] (lg/add-edge g v1 v2)
                   [g2 vx] (lg/add-vertex lg/empty-graph)
                   [g2 vy] (lg/add-vertex g2)]
               (lg/retrieve-edge g (second (lg/add-edge g2 vx vy)))
               => (throws ExceptionInfo #":type :.*/EDGE-NOT-IN-GRAPH")

               (:id (lg/retrieve-edge g e)) => (:id e))))

(facts "removal"
       (fact "remove vertex"
             (let [[g v1] (lg/add-vertex lg/empty-graph)]
               (lg/remove-vertex lg/empty-graph v1)
               => (throws ExceptionInfo #":type :.*/VERTEX-NOT-IN-GRAPH")

               (lg/remove-vertex g (first (lg/add-vertex lg/empty-graph)))
               => (throws ExceptionInfo #":type :.*/VERTEX-NOT-IN-GRAPH")

               (count (lg/vertices g)) => 1
               (count (lg/vertices (lg/remove-vertex g v1))) => 0))

       (fact "vertex removal removes connected edges"
             (let [[g v1] (lg/add-vertex lg/empty-graph)
                   [g v2] (lg/add-vertex g)
                   [g v3] (lg/add-vertex g)
                   [g e12] (lg/add-edge g v1 v2)
                   [g e13] (lg/add-edge g v1 v3)
                   [g e32] (lg/add-edge g v3 v2)
                   g (lg/remove-vertex g v2)]
               (count (lg/vertices g)) => 2
               (count (lg/edges g)) => 1
               (:id (lg/retrieve-edge g e13)) => (:id e13)

               (lg/retrieve-edge g e12)
               => (throws ExceptionInfo #":type :.*/EDGE-NOT-IN-GRAPH")))

       (fact "remove edge"
             (let [[g v1] (lg/add-vertex lg/empty-graph)
                   [g v2] (lg/add-vertex g)
                   [g e] (lg/add-edge g v1 v2)]
               (lg/remove-edge lg/empty-graph e)
               => (throws ExceptionInfo #":type :.*/EDGE-NOT-IN-GRAPH")

               (lg/remove-edge g (second (lg/add-edge g v1 v2)))
               => (throws ExceptionInfo #":type :.*/EDGE-NOT-IN-GRAPH")

               (count (lg/edges g)) => 1
               (count (lg/edges (lg/remove-edge g e))) => 0)))
