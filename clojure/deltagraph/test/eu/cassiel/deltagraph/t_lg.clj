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

               (:id (lg/retrieve-edge g e)) => (:id e)))

       (fact "connected"
             (let [[g v1] (lg/add-vertex lg/empty-graph)
                   [g v2] (lg/add-vertex g)
                   [g v3] (lg/add-vertex g)
                   [g v4] (lg/add-vertex g)
                   [g e1] (lg/add-edge g v1 v2)
                   [g e2] (lg/add-edge g v3 v1)
                   v1-connected (lg/connected g v1)]
               (count v1-connected) => 2
               (v1-connected v2) => truthy
               (v1-connected v3) => truthy
               (v1-connected v4) => falsey)))

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

(facts "dictionaries"
       (fact "add dictionary item to vertex"
             (let [[g v1] (lg/add-vertex lg/empty-graph)
                   d (assoc (:properties v1) "ANSWER" 42)
                   [g v2] (lg/put-dictionary g :vertices v1 d)
                   v3 (lg/retrieve-vertex g v1)]
               ((:properties v2) "ANSWER") => 42
               ((:properties v3) "ANSWER") => 42
               ((:properties v1) "ANSWER") => falsey))

       (fact "add dictionary item to edge"
             (let [[g v1] (lg/add-vertex lg/empty-graph)
                   [g v2] (lg/add-vertex g)
                   [g v3] (lg/add-vertex g)
                   [g e12] (lg/add-edge g v1 v2)
                   [g e31] (lg/add-edge g v3 v1)
                   d12 (assoc (:properties e12) "E12" "1-2")
                   d31 (assoc (:properties e31) "E31" "3-1")
                   [g e12'] (lg/put-dictionary g :edges e12 d12)
                   [g e31'] (lg/put-dictionary g :edges e31 d31)]
               ((:properties (lg/retrieve-edge g e12)) "E12") => "1-2"
               ((:properties (lg/retrieve-edge g e12')) "E12") => "1-2"
               ((:properties (lg/retrieve-edge g e12)) "E31") => falsey
               ((:properties (lg/retrieve-edge g e12')) "E31") => falsey
               ((:properties (lg/retrieve-edge g e31)) "E12") => falsey
               ((:properties (lg/retrieve-edge g e31')) "E12") => falsey
               ((:properties (lg/retrieve-edge g e31)) "E31") => "3-1"
               ((:properties (lg/retrieve-edge g e31')) "E31") => "3-1")))

(facts "change history"
       (fact "vertices"
             (let [[g v1] (lg/add-vertex lg/empty-graph)
                   [g v2] (lg/add-vertex g)
                   g (lg/remove-vertex g v1)
                   hist (vec (:change-history g))]
               (count hist) => 3

               (map :modtype hist)
               => [:vertex-removed :vertex-added :vertex-added]

               (get-in hist [2 :old-node]) => nil
               (get-in hist [2 :new-node :id]) => (:id v1)

               (get-in hist [1 :old-node]) => nil
               (get-in hist [1 :new-node :id]) => (:id v2)

               (get-in hist [0 :old-node :id]) => (:id v1)
               (get-in hist [0 :new-node]) => nil))

       (fact "vertex removal causes purge"
             (let [[g v1] (lg/add-vertex lg/empty-graph)
                   [g v2] (lg/add-vertex g)
                   [g e] (lg/add-edge g v1 v2)
                   g (lg/remove-vertex g v1)
                   hist (vec (:change-history g))]
               (count hist) => 5        ; Removal of vertex also removes edges.

               ; Orphaned edges removed (order undefined) before vertex.
               (map :modtype hist)
               => [:vertex-removed :edge-removed
                   :edge-added :vertex-added :vertex-added]

               (get-in hist [1 :old-node :id]) => (:id e)
               (get-in hist [1 :new-node]) => nil

               (get-in hist [0 :old-node :id]) => (:id v1)
               (get-in hist [0 :new-node]) => nil))

       (fact "edges"
             (let [[g v1] (lg/add-vertex lg/empty-graph)
                   [g v2] (lg/add-vertex g)
                   [g e] (lg/add-edge g v1 v2)
                   g (lg/remove-edge g e)
                   hist (vec (:change-history g))]
               (count hist) => 4

               (map :modtype hist)
               => [:edge-removed :edge-added :vertex-added :vertex-added]

               (get-in hist [1 :old-node]) => nil
               (get-in hist [1 :new-node :id]) => (:id e)

               (get-in hist [0 :old-node :id]) => (:id e)
               (get-in hist [0 :new-node]) => nil)))
