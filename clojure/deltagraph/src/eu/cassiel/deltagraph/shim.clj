(ns eu.cassiel.deltagraph.shim
  (:import (eu.cassiel.deltagraph.lg IGraph)))

(def emptyGraph
  (reify IGraph))
