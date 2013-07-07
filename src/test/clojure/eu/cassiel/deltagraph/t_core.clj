(ns eu.cassiel.deltagraph.t-core
  (:use midje.sweet)
  (:require (eu.cassiel.deltagraph [core :as c])))

(fact "foo"
      "A" => "A")

(fact "interop"
      (.doSomeJunk (c/doit) 99) => -99)
