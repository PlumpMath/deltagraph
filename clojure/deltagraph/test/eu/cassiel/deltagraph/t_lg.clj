(ns eu.cassiel.deltagraph.t-lg
  (:use midje.sweet)
  (:require (eu.cassiel.deltagraph [lg :as lg])))

(fact "vertex attributes"
      (-> (lg/new-vertex)
          (lg/add-attribute :A 44)
          (lg/add-attribute :B "B")
          (lg/get-vertex-attributes)) => {:A 44 :B "B"})
