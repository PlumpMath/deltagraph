(ns eu.cassiel.deltagraph.shim
  "Java shims for namespace `lg`."
  (:require (eu.cassiel.deltagraph [lg :as lg]))
  (:import (eu.cassiel.deltagraph IDict)
           (eu.cassiel.deltagraph.lg IVertex IEdge IGraph IGraphPlus)
           (java.util Set)))

(defn- dict
  "Create IDict from hash table."
  [hashtab]
  (reify IDict
    (^IDict putProperty [_ ^String key ^Object value]
      (dict (assoc hashtab key value)))

    (^Object getProperty [_ ^String key]
      (hashtab key))

    (repr [_] hashtab)))

(declare graph)

(defn- graph-plus [wrapper [g x]]
  (reify IGraphPlus
    (^IGraph getGraph [_] (graph g))
    (^Object getItem [_] (wrapper x))))

(defn- vertex [v]
  (reify IVertex
    (^int getId [_] (:id v))

    (^IDict getDictionary [_] (dict (:properties v)))

    (^IGraphPlus putDictionary [_
                                ^IGraph g
                                ^IDict d]
      (graph-plus vertex (lg/put-dictionary (.repr g) :vertices v (.repr d))))

    (repr [_] v)))

(defn- edge [e]
  (reify IEdge
    (^int getId [_] (:id e))

    (^IDict getDictionary [_] (dict (:properties e)))

    (^IGraphPlus putDictionary [_
                                ^IGraph g
                                ^IDict d]
      (graph-plus edge (lg/put-dictionary (.repr g) :edges e (.repr d))))

    (^IVertex getOther [_
                        ^IGraph g
                        ^IVertex thisVertex]
      (vertex (lg/other (.repr g)
                        e
                        (.repr thisVertex))))

    (repr [_] e)))

(defn- graph [g]
  (reify IGraph
    (^IGraphPlus addVertex [_]
      (graph-plus vertex (lg/add-vertex g)))

    (^IGraph removeVertex [_ ^IVertex v]
      (graph (lg/remove-vertex g (.repr v))))

    (^IGraphPlus addEdge [_ ^IVertex v1 ^IVertex v2]
      (graph-plus edge (lg/add-edge g (.repr v1) (.repr v2))))

    (^IGraph removeEdge [_ ^IEdge e]
      (graph (lg/remove-edge g (.repr e))))

    (^Set getVertices [_]
      ;; Slight overkill here (and `getEdges`); set using object identity only.
      (set (map vertex (lg/vertices g))))

    (^Set getEdges [_]
      (set (map edge (lg/edges g))))

    (repr [_] g)))

(def emptyGraph
  (graph lg/empty-graph))
