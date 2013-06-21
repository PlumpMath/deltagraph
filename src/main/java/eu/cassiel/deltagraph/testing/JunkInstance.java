//	$Id$
//	$Source$

package eu.cassiel.deltagraph.testing;

import clojure.lang.RT;
import clojure.lang.Symbol;

public class JunkInstance {
	static {
		RT.var("clojure.core", "require").invoke(Symbol.intern("eu.cassiel.deltagraph.core"));		
	}

	public IJunkInterface instantiate() {
		return (IJunkInterface) RT.var("eu.cassiel.deltagraph.core", "doit").invoke();
	}
}
