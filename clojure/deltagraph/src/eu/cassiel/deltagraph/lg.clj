(ns eu.cassiel.deltagraph.lg
  "'Little Graph' package. The graph structure is linked together via vertex and
   node IDs to allow relatively lightweight functional updates."
  (:use [clojure.core.incubator :only [dissoc-in]]
        [slingshot.slingshot :only [try+ throw+]]))

(def ^{:private true} new-stamp
  (let [stamp (atom 0)]
    #(swap! stamp inc)))

(def
  ^{:doc "The singleton empty graph."}
  empty-graph
  {:edges {}
   :vertices {}})

(defn add-vertex
  "Create new vertex (with unique ID). In expectation of alternate representations
   where IDs are indices into internal data structures, a vertex is added
   immediately to a graph, yielding a new graph."
  [g]
  (let [id (new-stamp)
        v {:id id :properties {}}]
    [(assoc-in g [:vertices id] v) v]))

(defn add-edge
  "Create a new edge with unique ID between two vertices. As for vertices,
   add immediately to a graph. Fail if either vertex is not present."
  [{:keys [vertices] :as g}
   {from-id :id}
   {to-id :id}]
  (cond (not (get vertices from-id))
        ;; TODO: in other representations, we'll need a better vertex-present check.
        (throw+ {:type ::VERTEX-NOT-PRESENT :id from-id})

        (not (get vertices to-id))
        (throw+ {:type ::VERTEX-NOT-PRESENT :id to-id})

        :else
        (let [id (new-stamp)
              e {:id id
                 :from-v from-id
                 :to-v to-id
                 :properties {}}]
          [(assoc-in g [:edges id] e) e])))

(defn other
  "Opposite vertex along an edge. TODO: should probably make sure the edge is
   in the graph."
  [{:keys [vertices]} {:keys [from-v to-v] :as e} v]
  (cond (= from-v (:id v))
        (or (get vertices to-v)
            (throw+ [:type ::VERTEX-NOT-PRESENT :id to-v]))

        (= to-v (:id v))
        (or (get vertices from-v)
            (throw+ [:type ::VERTEX-NOT-PRESENT :id to-v]))

        :else
        (throw+ [:type ::VERTEX-NOT-IN-EDGE :v-id (:id v) :e-id (:id e)])))

(defn retrieve-vertex
  "Retrieve 'current' vertex from `g` corresponding to (same ID as) `v`."
  [{:keys [vertices]} v]
  (or (get vertices (:id v))
      (throw+ [:type ::VERTEX-NOT-IN-GRAPH :id (:id v)])))

(defn retrieve-edge
  "Retrieve 'current' edge from `e` corresponding to (same ID as) `e`."
  [{:keys [edges]} e]
  (or (get edges (:id e))
      (throw+ [:type ::EDGE-NOT-IN-GRAPH :id (:id e)])))

(defn remove-edge
  "Remove an edge from a graph; vertices untouched."
  [g e]
  (let [path [:edges (:id e)]]
    (if (get-in g path)
      (dissoc-in g path)
      (throw+ [:type ::EDGE-NOT-IN-GRAPH :id (:id e)]))))

(defn remove-vertex
  "Remove a vertex from a graph (as well as any edges attached to it)."
  [g {:keys [id] :as v}]
  (let [path [:vertices id]]
    (if (get-in g path)
      (let [edge-includes-v? (fn [e] (#{(:from-v e) (:to-v e)} id))
            g' (reduce remove-edge g (filter edge-includes-v? (vals (:edges g))))]
        (dissoc-in g' path))
      (throw+ [:type ::VERTEX-NOT-IN-GRAPH :id id]))))

(defn vertices [{vv :vertices}]
  (vals vv))

(defn edges [{ee :edges}]
  (vals ee))
