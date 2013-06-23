(ns eu.cassiel.deltagraph.shim
  "Java shims for namespace `lg`."
  (:require (eu.cassiel.deltagraph [lg :as lg]))
  (:import (eu.cassiel.deltagraph.lg IVertex IEdge IGraph IGraphPlus)
           (java.util List)))

(defn- vertex [v]
  (reify IVertex
    (^int getId [_] (:id v))

    (repr [_] v)))

(defn- edge [e]
  (reify IEdge
    (^int getId [_] (:id e))

    (^IVertex getOther [_
                        ^IGraph g
                        ^IVertex thisVertex]
      (vertex (lg/other (.repr g)
                        e
                        (.repr thisVertex))))

    (repr [_] e)))

(declare graph)

(defn- graph-plus [wrapper [g x]]
  (reify IGraphPlus
    (^IGraph getGraph [_] (graph g))
    (^Object getItem [_] (wrapper x))))

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

    (^List getVertices [_]
      (map vertex (lg/vertices g)))

    (^List getEdges [_]
      (map edge (lg/edges g)))

    (repr [_] g)))

(def emptyGraph
  (graph lg/empty-graph))
