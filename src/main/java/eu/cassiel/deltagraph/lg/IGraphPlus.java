//	$Id$
//	$Source$

package eu.cassiel.deltagraph.lg;

/**	Pairing of graph plus additional item - used mainly for graph-mutating
	functions which return a new graph plus some newly-created or altered
	item.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net

	@param <T> the type of the additional item
 */

public interface IGraphPlus<T> {
	IGraph getGraph();
	T getItem();
}
