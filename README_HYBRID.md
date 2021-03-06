`-*- word-wrap: t; -*-`

# Java and Clojure Project Interop

## Background

We want to develop purely in Clojure but present an API which is constructed purely from Java interfaces (plus, I guess, a tiny bit of "real" Java as entry point), for transparent use from a Java environment. So: this is a hybrid Leiningen project plus an Eclipse Java project. The Java needs to call into Clojure (for actual implementation code), while the Clojure needs access to the Java (for interfaces which it is going to [reify](http://clojuredocs.org/clojure_core/clojure.core/reify)). And we want to be developing in Clojure without worrying about `gen-class` to keep the Java world up-to-date.

On the Eclipse side: a standard Maven Java project, and a Leiningen project also at top level. Both Maven (standalone or via Eclipse) and Leiningen will generate a tree of class files under `target`, but these should be the same regardless of which tool makes them.

## Clojure from Java

This side of the interop (invoking Clojure from Java) is fairly well documented: see [JunkInstance](src/main/java/eu/cassiel/deltagraph/testing/JunkInstance.java) or [Main](src/main/java/eu/cassiel/deltagraph/testing/Main.java). It's pretty much just this:

	static {
		RT.var("clojure.core", "require")
                        .invoke(Symbol.intern("eu.cassiel.deltagraph.core"));
	}

        ...
	(IJunkInterface) RT.var("eu.cassiel.deltagraph.core", "doit").invoke();
        ...

The root of the Clojure source tree (in our case, `src/main/clojure`) needs to be a source directory in Eclipse, to get it into the classpath. (It can be added as a library directory instead, but that probably doesn't get it copied into JARs during a Maven build - see below.)

## Java from Clojure

This is standard interop: we just `reify`:

        (ns eu.cassiel.deltagraph.core
            (:import (eu.cassiel.deltagraph.testing IJunkInterface))
            (:gen-class :main true))

        ...
        (reify IJunkInterface
            (^int doSomeJunk [this ^int i] (- i)))

We need to see the Java interfaces from Clojure, so our [project.clj](clojure/deltagraph/project.clj) contains this:

        :java-source-paths ["src/main/java"]

At this stage, we can fire off tests in jUnit (either from Eclipse or `mvn test`) and calls into Clojure work fine. Similarly, Midje unit tests in Clojure call into Java without problems.

## Usage

If the Clojure source directory is added to the Eclipse project, then the top-level `target` directory will contain both (a copy of) the Clojure sources - possibly after a refresh if they've been edited externally - and the compiled Java classes, ready for adding to a classpath for development.

For actual development, it makes sense to spawn a thread with a network REPL into a running system. I leave this as an exercise. (I have one in the [Cubewar server](https://github.com/cassiel/cubewar).)

## Main Programs

Just for fun, we've built console main programs on both sides. Both take a single integer as argument, and each calls its counterpart with the counter decremented, stopping at zero:

        I am the main program in Java.
        Across to Clojure at: 5
        I am the main program in Clojure.
        Across to Java at: 4
        I am the main program in Java.
        Across to Clojure at: 3
        I am the main program in Clojure.
        Across to Java at: 2
        I am the main program in Java.
        Across to Clojure at: 1
        I am the main program in Clojure.
        Across to Java at: 0
        I am the main program in Java.

This can be fired off on the Java side from Eclipse, or the Clojure side via `lein run`.

## Continuous Integration Testing

Midje testing (via `lein midje`) works fine - `project.clj` rolls in the Java code so that Clojure can access it. Out-of-the-box Maven testing (via `mvn test`) will appear to work in the Eclipse project directory, but that's only because Eclipse has "compiled" (i.e. copied) the Clojure sources into the Java target directory; in a clean checkout, Maven will fail. Accordingly, `pom.xml` has the Clojure source tree added explicitly to the classpath.

Both sets of tests work (in sequence) on [Travis CI](https://travis-ci.org/cassiel/deltagraph). Predictably, the [Travis configuration](https://github.com/cassiel/deltagraph/blob/master/.travis.yml) is slightly bespoke (it can be simplified by making it language-agnostic, but I like to keep the Clojure/Java nature and dependencies explicit).

## Builds

Standalone builds work fine as well. From the Java side:

        mvn package
        cd target/
        java -cp [...]/clojure-1.5.1.jar:eu.cassiel.deltagraph-0.0.1-SNAPSHOT.jar \
                eu.cassiel.deltagraph.testing.Main 5

(Note the explicit Clojure JAR in there; we'll probably be using additional libraries by the time you read this, so check `project.clj`.) Obviously I've not bothered with the tedious work of getting the manifest sorted for identifying the main class.

From the Clojure side:

        lein uberjar
        cd target/
        java -jar eu.cassiel.deltagraph-clj-0.1.0-SNAPSHOT-standalone.jar 5

(This is the sole reason for the `:gen-class` in the [main Clojure namespace](clojure/deltagraph/src/eu/cassiel/deltagraph/core.clj).)
