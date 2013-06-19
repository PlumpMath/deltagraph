//	$Id$
//	$Source$

package eu.cassiel.deltagraph.testing;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.cassiel.deltagraph.testing.JunkInstance;
import eu.cassiel.deltagraph.testing.JunkInterface;

public class JunkInstanceTest {
	@Test
	public void testMakeInstance() {
		JunkInterface obj = new JunkInstance().instantiate();
		assertEquals("JunkInterface", -99, obj.doSomeJunk(99));
	}
}
