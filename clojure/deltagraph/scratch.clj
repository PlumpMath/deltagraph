(ns user
  (:require (eu.cassiel.deltagraph [core :as c]
                                   [lg :as lg]
                                   [shim :as shim]
                                   [lib :as lib]))
  (:import (eu.cassiel.deltagraph.testing JunkInterface)))

(c/doit)

(lg/get-vertex-attributes
 (lg/new-vertex))

(lg/get-vertex-attributes
 (lg/add-attribute
  (lg/new-vertex) :A 45))

(fnext [1 2 34])

(lg/add-vertex lg/empty-graph)

(let [g lg/empty-graph
      [g v1] (lg/add-vertex g)
      [g v2] (lg/add-vertex g)]
  (lg/add-edge g v1 v2))

(.addVertex shim/emptyGraph)

(extends? lg/empty-graph java.util.HashMap)

(class #{1 2 3})
(class (clojure.set/union (sorted-set-by > 1 2 3)
                          (sorted-set-by > 9 8 7)))

(let [g lg/empty-graph
      [g v1] (lg/add-vertex g)
      [g v2] (lg/add-vertex g)
      [g e] (lg/add-edge g v1 v2)]
  (lg/connected g v2))

(set (map inc #{1 2 3}))

(lib/assoc-alter
 (assoc-in nil [:A :B] 4)
 [:A :B] inc)

(let [g lg/empty-graph
      [g v1] (lg/add-vertex g)
      [g v2] (lg/add-vertex g)
      [g e] (lg/add-edge g v1 v2)
      g (lg/remove-vertex g v1)]
  {:A (:change-history g)
   :B (get-in (vec (:change-history g)) [2 :new-node :id])})

(= (set [1 2])
   (set [2 1]))

(.getId
 (.getNew
  (shim/idiff-modification {:modtype :edge-added
                            :new-node {:id 2222}})))
