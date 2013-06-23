//	$Id$
//	$Source$

package eu.cassiel.deltagraph;

import clojure.lang.RT;
import clojure.lang.Symbol;
import eu.cassiel.deltagraph.lg.IGraph;

public class LG {
	static {
		RT.var("clojure.core", "require").invoke(Symbol.intern("eu.cassiel.deltagraph.shim"));		
	}

	/**	Entry point: fixed empty graph. */
	public static IGraph emptyGraph =
			(IGraph) RT.var("eu.cassiel.deltagraph.shim", "emptyGraph").get();
}
