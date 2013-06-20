(ns user
  (:require (eu.cassiel.deltagraph [core :as c]
                                   [lg :as lg]))
  (:import (eu.cassiel.deltagraph.testing JunkInterface)))

(c/doit)

(lg/get-vertex-attributes
 (lg/new-vertex))

(lg/get-vertex-attributes
 (lg/add-attribute
  (lg/new-vertex) :A 45))
