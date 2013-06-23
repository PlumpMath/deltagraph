(ns eu.cassiel.deltagraph.shim
  "Java shims for namespace `lg`."
  (:require (eu.cassiel.deltagraph [lg :as lg]))
  (:import (eu.cassiel.deltagraph.lg IVertex IGraph IGraphPlus)
           (java.util List)))

(defn- vertex [v]
  (reify IVertex))

(declare graph)

(defn- graph-plus [[g x]]
  (reify IGraphPlus
    (^IGraph getGraph [this] (graph g))
    (^Object getItem [this] x)))

(defn- graph [g]
  (reify IGraph
    (^IGraphPlus addVertex [this]
      (graph-plus (lg/add-vertex g)))

    (^List getVertices [this]
      (map vertex (lg/vertices g)))))

(def emptyGraph
  (graph lg/empty-graph))
