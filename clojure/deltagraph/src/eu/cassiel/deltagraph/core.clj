(ns eu.cassiel.deltagraph.core
  (:import (eu.cassiel.deltagraph.testing JunkInterface)))

(defn doit
  "Called from Java via jUnit."
  []
  (reify JunkInterface
    (^int doSomeJunk [this ^int i] (- i))))
