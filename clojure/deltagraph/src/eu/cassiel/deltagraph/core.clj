(ns eu.cassiel.deltagraph.core
  (:import (eu.cassiel.deltagraph.testing IJunkInterface Main))
  (:gen-class :main true))

(defn doit
  "Called from Java via jUnit."
  []
  (reify IJunkInterface
    (^int doSomeJunk [this ^int i] (- i))))

(defn -main [& args]
  (println "I am the main program in Clojure.")

  (when (> (count args) 0)
    (let [i (Integer/parseInt (first args))]
      (println (str "Across to Java at: " i))
      (Main/main (into-array [(str (dec i))])))))
