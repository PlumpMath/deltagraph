(ns eu.cassiel.deltagraph.lib)

(defn assoc-alter
  "Do an `assoc-in` with a function."
  [m keys f]
  (assoc-in m keys (f (get-in m keys))))
