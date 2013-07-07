(defproject eu.cassiel.deltagraph-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :java-source-paths ["src/main/java"]
  :main eu.cassiel.deltagraph.core
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.incubator "0.1.3"]
                 [slingshot "0.10.3"]]
  :plugins [[lein-midje "3.0.1"]]
  :profiles
  {:dev {:dependencies [[midje "1.5.1"]]}})
