//	$Id$
//	$Source$

package eu.cassiel.deltagraph;

/**	Marker interface for retrieving original Clojure objects from the
  	reified Java wrappers; called inside Clojure.
 
	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface IWrapped {
	/**	the original Clojure representation of this object. */
	Object repr();
}
