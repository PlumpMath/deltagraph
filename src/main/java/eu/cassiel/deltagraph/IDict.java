//	$Id$
//	$Source$

package eu.cassiel.deltagraph;

/**	Immutable dictionary interface.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface IDict extends IWrapped {
	/**	Add an item to a dictionary. */
	IDict putProperty(String key, Object value);

	/**	Retrieve an item from a dictionary, return null if none present. */
	Object getProperty(String key);
}
