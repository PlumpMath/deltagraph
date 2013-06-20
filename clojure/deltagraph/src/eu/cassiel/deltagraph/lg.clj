(ns eu.cassiel.deltagraph.lg
  "'Little Graph' package. The graph structure is linked together via vertex and
   node IDs to allow relatively lightweight functional updates."
  (:use [slingshot.slingshot :only [try+ throw+]]))

(def ^{:private true} new-stamp
  (let [stamp (atom 0)]
    #(swap! stamp inc)))

(def
  ^{:doc "The singleton empty graph."}
  empty-graph
  {:edges {}
   :vertices {}})

(defn new-vertex
  "Create new vertex with unique ID."
  []
  {:id (new-stamp)
   :attributes {}})

(defn put-vertex
  "Add vertex to a graph, or replace it (depending on whether `id` is unique).
   Returns new graph."
  [g v]
  (assoc-in g [:vertices (:id v)] v))

(defn new-edge
  "Create a new edge with unique ID between two vertices (not necessarily in a graph)."
  [v1 v2]
  {:id (new-stamp)
   :from-v (:id v1)
   :to-v (:id v2)
   :attributes {}})

(defn put-edge
  "Add an edge to a graph. Fail if either vertex ID is not present. Returns new graph."
  [{:keys [vertices] :as g}
   {:keys [id from-v to-v] :as e}]
  (cond (not (get vertices from-v))
        (throw+ {:type ::VERTEX-NOT-PRESENT :id from-v})

        (not (get vertices to-v))
        (throw+ {:type ::VERTEX-NOT-PRESENT :id to-v})

        :else
        (assoc-in g [:edges id] e)))

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

(defn vertices [{vv :vertices}]
  (vals vv))

(defn edges [{ee :edges}]
  (vals ee))
