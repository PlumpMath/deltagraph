(ns eu.cassiel.deltagraph.shim
  "Java shims for namespace `lg`."
  (:require (eu.cassiel.deltagraph [lg :as lg]))
  (:import (eu.cassiel.deltagraph IDict IDiff$Modification IDiff$ModType IProperty)
           (eu.cassiel.deltagraph.lg IVertex IEdge IGraph IGraphPlus)
           (java.util Set List)))

(declare vertex)
(declare edge)

(defn- shim-node
  "The function for wrapping a node (edge, vertex, ...) depending on the `modtype`."
  [modtype x]
  (when x
    (({:vertex-added vertex
       :vertex-removed vertex
       :edge-added edge
       :edge-removed edge} modtype) x)))

(defn- idiff-modification
  "Create an IDiff.Modification entry. (At the moment, IDiff itself isn't used.)"
  [{:keys [modtype
           old-node new-node
           key
           old-value new-value]}]
  (reify IDiff$Modification
    (^IDiff$ModType getModType [_]
      ({:vertex-added IDiff$ModType/VERTEX_ADDED
        :vertex-removed IDiff$ModType/VERTEX_REMOVED
        :edge-added IDiff$ModType/EDGE_ADDED
        :edge-removed IDiff$ModType/EDGE_REMOVED
        :property-added IDiff$ModType/PROPERTY_ADDED
        :property-removed IDiff$ModType/PROPERTY_REMOVED
        :property-changed IDiff$ModType/PROPERTY_CHANGED} modtype))

    (^Object getOld [_] (shim-node modtype old-node))
    (^Object getNew [_] (shim-node modtype new-node))
    (^IProperty getKey [_] nil)
    (^Object getOldValue [_] nil)
    (^Object getNewValue [_] nil)))

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

    (^IVertex retrieveVertex [_ ^IVertex v]
      (vertex (lg/retrieve-vertex g (.repr v))))

    (^IGraph removeVertex [_ ^IVertex v]
      (graph (lg/remove-vertex g (.repr v))))

    (^IGraphPlus addEdge [_ ^IVertex v1 ^IVertex v2]
      (graph-plus edge (lg/add-edge g (.repr v1) (.repr v2))))

    (^IEdge retrieveEdge [_ ^IEdge e]
      (edge (lg/retrieve-edge g (.repr e))))

    (^IGraph removeEdge [_ ^IEdge e]
      (graph (lg/remove-edge g (.repr e))))

    (^Set getVertices [_]
      ;; Slight overkill here (and `getEdges`); set using object identity only.
      (set (map vertex (lg/vertices g))))

    (^Set getEdges [_]
      (set (map edge (lg/edges g))))

    (^List getChangeHistory [_]
      (map idiff-modification (:change-history g)))

    (repr [_] g)))

(def emptyGraph
  (graph lg/empty-graph))
