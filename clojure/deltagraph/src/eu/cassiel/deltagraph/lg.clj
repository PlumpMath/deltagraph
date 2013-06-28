(ns eu.cassiel.deltagraph.lg
  "'Little Graph' package. The graph structure is linked together via vertex and
   node IDs to allow relatively lightweight functional updates."
  (:require (eu.cassiel.deltagraph [lib :as lib]))
  (:use [clojure.set :only [union difference intersection]]
        [clojure.core.incubator :only [dissoc-in]]
        [slingshot.slingshot :only [try+ throw+]]))

(def ^{:private true} new-stamp
  (let [stamp (atom 0)]
    #(swap! stamp inc)))

(def
  ^{:doc "The singleton empty graph."}
  empty-graph
  {:edges {}
   :vertices {}
   :change-history nil})

(defn add-vertex
  "Create new vertex (with unique ID). In expectation of alternate representations
   where IDs are indices into internal data structures, a vertex is added
   immediately to a graph, yielding a new graph."
  [g]
  (let [id (new-stamp)
        v {:id id :properties {}}]
    [(-> g
         (assoc-in [:vertices id] v)
         (lib/assoc-alter [:change-history]
                          (partial cons {:modtype :vertex-added
                                         :new-node v})))
     v]))

(defn compare-by-id
  "Vertex/edge comparison is timestamp comparison."
  [item1 item2]
  (compare (:id item1) (:id item2)))

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
          [(-> g
               (assoc-in [:edges id] e)
               (lib/assoc-alter [:change-history] (partial cons {:modtype :edge-added
                                                                 :new-node e})))
           e])))

(defn other
  "Opposite vertex along an edge."
  [{:keys [vertices]} {:keys [from-v to-v] :as e} v]
  (cond (= from-v (:id v))
        (or (get vertices to-v)
            (throw+ [:type ::VERTEX-NOT-PRESENT :id to-v]))

        (= to-v (:id v))
        (or (get vertices from-v)
            (throw+ [:type ::VERTEX-NOT-PRESENT :id to-v]))

        :else
        (throw+ [:type ::VERTEX-NOT-IN-EDGE :v-id (:id v) :e-id (:id e)])))

(defn connected
  "Get all connected vertices to `v` (as set, since a candidate vertex might
   show up multiple times via multiple edges)."
  [{:keys [vertices edges]} v]
  (let [outgoing-edges (filter (fn [e] (when (= (:from-v e) (:id v)) e)) (vals edges))
        incoming-edges (filter (fn [e] (when (= (:to-v e) (:id v)) e)) (vals edges))]
    (union (apply sorted-set-by compare-by-id (map (comp vertices :to-v) outgoing-edges))
           (apply sorted-set-by compare-by-id (map (comp vertices :from-v) incoming-edges)))))

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
      (-> g
          (dissoc-in path)
          (lib/assoc-alter [:change-history] (partial cons {:modtype :edge-removed
                                                            :old-node e})))
      (throw+ [:type ::EDGE-NOT-IN-GRAPH :id (:id e)]))))

(defn remove-vertex
  "Remove a vertex from a graph (as well as any edges attached to it)."
  [g {:keys [id] :as v}]
  (let [path [:vertices id]]
    (if (get-in g path)
      (let [edge-includes-v? (fn [e] (#{(:from-v e) (:to-v e)} id))
            g' (reduce remove-edge g (filter edge-includes-v? (vals (:edges g))))]
        (-> g'
            (dissoc-in path)
            (lib/assoc-alter [:change-history]
                             (partial cons {:modtype :vertex-removed
                                            :old-node v}))))
      (throw+ [:type ::VERTEX-NOT-IN-GRAPH :id id]))))

(defn vertices
  "Retrieve set of vertices in graph."
  [{vv :vertices}]
  (apply sorted-set-by compare-by-id (vals vv)))

(defn edges
  "Retrieve set of edges in graph."
  [{ee :edges}]
  (apply sorted-set-by compare-by-id (vals ee)))

(defn put-dictionary
  "Works for both edges and vertices. In the history we'll plant the removes,
   then the changes, then the adds, but within each category there's no guarantee
   of order."
  [g collection-tag item dict]
  (let [old-dict (get-in g [collection-tag (:id item) :properties])
        old-keys (set (keys old-dict))
        new-keys (set (keys dict))
        removed (difference old-keys new-keys)
        added (difference new-keys old-keys)
        common (intersection old-keys new-keys)
        item' (assoc item :properties dict)
        ;; Note that the `old-node` and `new-node` values are those before and after
        ;; the entire `put-dictionary` call, and are the same in all of the change
        ;; entries. (TODO: Do we want incremental ones?)
        change-history (as-> (:change-history g) ch
                             ;; Removed items:
                             (reduce (fn [ch k] (cons {:modtype :property-removed
                                                      :old-node item
                                                      :new-node item'
                                                      :key k
                                                      :old-value (get old-dict k)
                                                      :new-value nil} ch))
                                     ch
                                     removed)
                             ;; Changed items (but only when the actual property changes):
                             (reduce (fn [ch k] (let [old-value (get old-dict k)
                                                     new-value (get dict k)]
                                                 (if (.equals old-value new-value)
                                                   ch
                                                   (cons
                                                    {:modtype :property-changed
                                                     :old-node item
                                                     :new-node item'
                                                     :key k
                                                     :old-value old-value
                                                     :new-value new-value} ch))))
                                     ch
                                     common)
                             ;; Added items:
                             (reduce (fn [ch k] (cons {:modtype :property-added
                                                      :old-node item
                                                      :new-node item'
                                                      :key k
                                                      :old-value nil
                                                      :new-value (get dict k)} ch))
                                     ch
                                     added))]
    [(-> g
         (assoc-in [collection-tag (:id item)] item')
         (assoc :change-history change-history
                :common common))
     item']))
