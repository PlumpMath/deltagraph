(ns eu.cassiel.deltagraph.lg)

(defprotocol VERTEX
  (get-connected [this] "Set of all opposite vertices on edges attached to this vertex.")
  (get-vertex-attributes [this] "Hash of all attributes at this vertex."))

(defprotocol EDGE
  (get-other [this v] "Vertex at the other end of this edge from `v`.
                       (Error if `v` not on this edge?)")
  (get-edge-attributes [this] "Hash of all attributes on this edge."))

(def stamp (atom 0))

(defn- new-stamp [] (swap! stamp inc))

(defn new-vertex
  "Empty vertex."
  []
  (reify VERTEX
    (get-connected [_] #{})
    (get-vertex-attributes [_] {})))

(defn add-attribute [v k value]
  (reify VERTEX
    (get-connected [_] (get-connected v))
    (get-vertex-attributes [_] (assoc (get-vertex-attributes v) k value))))
