(ns eu.cassiel.deltagraph.shim
  "Java shims for namespace `lg`."
  (:require (eu.cassiel.deltagraph [lg :as lg]))
  (:import (eu.cassiel.deltagraph.lg IVertex IEdge IGraph IGraphPlus)
           (java.util List)))

(defn- vertex [v]
  (reify IVertex
    (^int getId [this] (:id v))))

(defn- edge [e]
  (reify IEdge
    (^int getId [this] (:id e))))

(declare graph)

(defn- graph-plus [wrapper [g x]]
  (reify IGraphPlus
    (^IGraph getGraph [this] (graph g))
    (^Object getItem [this] (wrapper x))))

(defn- graph [g]
  (reify IGraph
    (^IGraphPlus addVertex [this]
      (graph-plus vertex (lg/add-vertex g)))

    (^IGraphPlus addEdge [this ^IVertex v1 ^IVertex v2]
      (graph-plus edge (lg/add-edge-ids g (.getId v1) (.getId v2))))

    (^List getVertices [this]
      (map vertex (lg/vertices g)))

    (^List getEdges [this]
      (map edge (lg/edges g)))))

(def emptyGraph
  (graph lg/empty-graph))
