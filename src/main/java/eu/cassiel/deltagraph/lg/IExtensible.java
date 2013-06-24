//	$Id$
//	$Source$

package eu.cassiel.deltagraph.lg;

import eu.cassiel.deltagraph.IDict;
import eu.cassiel.deltagraph.IWrapped;

/**	Objects with property dictionaries. Slightly fiddly because of the attempt
	to things type-safe and immutable.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface IExtensible<T> extends IWrapped {
	/**	Retrieve the (immutable) dictionary. */
	IDict getDictionary();
	
	/** Modify a dictionary of this element on the graph, returning a new
	 	graph and element. */
	
	IGraphPlus<T> putDictionary(IGraph g, IDict dictionary);
}
