//	$Id$
//	$Source$

package eu.cassiel.deltagraph.testing;

import clojure.lang.RT;
import clojure.lang.Symbol;

public class Main {
	static {
		RT.var("clojure.core", "require").invoke(Symbol.intern("eu.cassiel.deltagraph.core"));
	}

	public static void main(String[] argv) {
		System.out.println("I am the main program in Java.");

		if (argv.length > 0) {
			int i = Integer.parseInt(argv[0]);
			
			if (i > 0) {
				System.out.println("Across to Clojure at: " + i);
				RT.var("eu.cassiel.deltagraph.core", "-main").invoke("" + (i - 1));
			}
		}
	}
}
