//	$Id$
//	$Source$

package eu.cassiel.deltagraph;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.cassiel.deltagraph.lg.IGraph;

public class LGTest {
	@Test
	public void testEmptyGraph() {
		assertNotNull(LG.emptyGraph);
		assertTrue(LG.emptyGraph instanceof IGraph);
	}
}
