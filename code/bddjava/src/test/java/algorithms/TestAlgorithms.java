package algorithms;

import bddgraph.BddGraph;
import bddgraph.Edge;
import net.sf.javabdd.BDD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAlgorithms {

    private BddGraph bddGraph;
    private GraphSCCAlgorithm algorithm;

    @BeforeEach
    public void setUp(){
        List<Edge> edges = new ArrayList<>();

        edges.add(new Edge(0,1));
        edges.add(new Edge(1,0));
        edges.add(new Edge(2,3));
        edges.add(new Edge(2,7));
        edges.add(new Edge(3,7));
        edges.add(new Edge(4,0));
        edges.add(new Edge(4,5));
        edges.add(new Edge(4,8));
        edges.add(new Edge(4,9));
        edges.add(new Edge(5,6));
        edges.add(new Edge(6,10));
        edges.add(new Edge(7,11));
        edges.add(new Edge(9,5));
        edges.add(new Edge(10,5));
        edges.add(new Edge(11,6));
        edges.add(new Edge(11,3));

        bddGraph = new BddGraph(edges, 13);
    }

    @Test
    public void testLockstepShouldFindCorrectSCCs(){
        algorithm = new Lockstep(new NullLoggingStrategy());
        Set<BDD> sccs = algorithm.run(bddGraph);

        List<Set<Integer>> intSccs = sccs.stream().map(scc -> bddGraph.nodeSetToIntegerSet(scc)).collect(Collectors.toList());
        Set<Integer> expectedScc1 = new HashSet<>();
        expectedScc1.add(8);
        Set<Integer> expectedScc2 = new HashSet<>();
        expectedScc2.add(0);
        expectedScc2.add(1);
        Set<Integer> expectedScc3 = new HashSet<>();
        expectedScc3.add(4);
        Set<Integer> expectedScc4 = new HashSet<>();
        expectedScc4.add(9);
        Set<Integer> expectedScc5 = new HashSet<>();
        expectedScc5.add(2);
        Set<Integer> expectedScc6 = new HashSet<>();
        expectedScc6.add(3);
        expectedScc6.add(7);
        expectedScc6.add(11);
        Set<Integer> expectedScc7 = new HashSet<>();
        expectedScc7.add(5);
        expectedScc7.add(6);
        expectedScc7.add(10);
        Set<Integer> expectedScc8 = new HashSet<>();
        expectedScc8.add(12);

        assertEquals(8, intSccs.size());
        assertTrue(intSccs.contains(expectedScc1));
        assertTrue(intSccs.contains(expectedScc2));
        assertTrue(intSccs.contains(expectedScc3));
        assertTrue(intSccs.contains(expectedScc4));
        assertTrue(intSccs.contains(expectedScc5));
        assertTrue(intSccs.contains(expectedScc6));
        assertTrue(intSccs.contains(expectedScc7));
        assertTrue(intSccs.contains(expectedScc8));
    }

    @Test
    public void testLockstepWithTrimmingShouldFindCorrectSCCs(){
        algorithm = new LockstepWithTrimming(new NullLoggingStrategy());
        Set<BDD> sccs = algorithm.run(bddGraph);

        List<Set<Integer>> intSccs = sccs.stream().map(scc -> bddGraph.nodeSetToIntegerSet(scc)).collect(Collectors.toList());
        Set<Integer> expectedScc1 = new HashSet<>();
        expectedScc1.add(8);
        Set<Integer> expectedScc2 = new HashSet<>();
        expectedScc2.add(0);
        expectedScc2.add(1);
        Set<Integer> expectedScc3 = new HashSet<>();
        expectedScc3.add(4);
        Set<Integer> expectedScc4 = new HashSet<>();
        expectedScc4.add(9);
        Set<Integer> expectedScc5 = new HashSet<>();
        expectedScc5.add(2);
        Set<Integer> expectedScc6 = new HashSet<>();
        expectedScc6.add(3);
        expectedScc6.add(7);
        expectedScc6.add(11);
        Set<Integer> expectedScc7 = new HashSet<>();
        expectedScc7.add(5);
        expectedScc7.add(6);
        expectedScc7.add(10);
        Set<Integer> expectedScc8 = new HashSet<>();
        expectedScc8.add(12);

        assertEquals(8, intSccs.size());
        assertTrue(intSccs.contains(expectedScc1));
        assertTrue(intSccs.contains(expectedScc2));
        assertTrue(intSccs.contains(expectedScc3));
        assertTrue(intSccs.contains(expectedScc4));
        assertTrue(intSccs.contains(expectedScc5));
        assertTrue(intSccs.contains(expectedScc6));
        assertTrue(intSccs.contains(expectedScc7));
        assertTrue(intSccs.contains(expectedScc8));
    }

    @Test
    public void testLinearShouldFindCorrectSCCs(){
        algorithm = new Linear(new NullLoggingStrategy());
        Set<BDD> sccs = algorithm.run(bddGraph);

        List<Set<Integer>> intSccs = sccs.stream().map(scc -> bddGraph.nodeSetToIntegerSet(scc)).collect(Collectors.toList());
        Set<Integer> expectedScc1 = new HashSet<>();
        expectedScc1.add(8);
        Set<Integer> expectedScc2 = new HashSet<>();
        expectedScc2.add(0);
        expectedScc2.add(1);
        Set<Integer> expectedScc3 = new HashSet<>();
        expectedScc3.add(4);
        Set<Integer> expectedScc4 = new HashSet<>();
        expectedScc4.add(9);
        Set<Integer> expectedScc5 = new HashSet<>();
        expectedScc5.add(2);
        Set<Integer> expectedScc6 = new HashSet<>();
        expectedScc6.add(3);
        expectedScc6.add(7);
        expectedScc6.add(11);
        Set<Integer> expectedScc7 = new HashSet<>();
        expectedScc7.add(5);
        expectedScc7.add(6);
        expectedScc7.add(10);
        Set<Integer> expectedScc8 = new HashSet<>();
        expectedScc8.add(12);

        assertEquals(8, intSccs.size());
        assertTrue(intSccs.contains(expectedScc1));
        assertTrue(intSccs.contains(expectedScc2));
        assertTrue(intSccs.contains(expectedScc3));
        assertTrue(intSccs.contains(expectedScc4));
        assertTrue(intSccs.contains(expectedScc5));
        assertTrue(intSccs.contains(expectedScc6));
        assertTrue(intSccs.contains(expectedScc7));
        assertTrue(intSccs.contains(expectedScc8));
    }

}
