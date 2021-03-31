package BddGraph;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestBddGraph {

    private BddGraph bddGraph;
    private BDDFactory bddFactory;

    /*
                             ┌──┐
                     ┌───────►00│
                     │       └▲┌┘
                     │        ││
                     │        ││
                    ┌┴─┐     ┌┘▼┐     ┌──┐
                    │01│     │10├─────►11│
                    └──┘     └──┘     └──┘
     */

    private BDD node00;
    private BDD node01;
    private BDD node10;
    private BDD node11;

    @BeforeEach
    public void setUp(){
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 2));
        edges.add(new Edge(1, 0));
        edges.add(new Edge(2, 3));
        edges.add(new Edge(2, 0));
        bddGraph = new BddGraph(edges, 4);
        bddFactory = bddGraph.getBddFactory();

        node00 = bddFactory.nithVar(0).and(bddFactory.nithVar(1));
        node01 = bddFactory.nithVar(0).and(bddFactory.ithVar(1));
        node10 = bddFactory.ithVar(0).and(bddFactory.nithVar(1));
        node11 = bddFactory.ithVar(0).and(bddFactory.ithVar(1));
    }

    @Test
    public void testBddGraphGetsCreatedCorrectly() {
        BDD expectedEdge1 = node00.and(bddFactory.ithVar(2)).and(bddFactory.nithVar(3));
        BDD expectedEdge2 = node10.and(bddFactory.nithVar(2)).and(bddFactory.nithVar(3));
        BDD expectedEdge4 = node01.and(bddFactory.nithVar(2)).and(bddFactory.nithVar(3));
        BDD expectedEdge5 = node10.and(bddFactory.ithVar(2)).and(bddFactory.ithVar(3));

        assertTrue(bddGraph.getBdd().restrict(expectedEdge1).isOne());
        assertTrue(bddGraph.getBdd().restrict(expectedEdge2).isOne());
        assertTrue(bddGraph.getBdd().restrict(expectedEdge4).isOne());
        assertTrue(bddGraph.getBdd().restrict(expectedEdge5).isOne());
    }

    @Test
    public void testImgOfEmptyIsEmpty() {
        assertTrue(bddGraph.img(bddFactory.zero()).isZero());
    }

    @Test
    public void testImgOfNode11IsEmpty() {
        assertTrue(bddGraph.img(node11).isZero());
    }

    @Test
    public void testImgOfAllNodesIsAllNodesExcept01() {
        BDD allExcept01 = bddFactory.one().and(node01.not());
        assertEquals(allExcept01, bddGraph.img(bddFactory.one()));
    }

    @Test
    public void testImgOf10Is00And11() {
        BDD node00And11 = node00.or(node11);
        assertEquals(node00And11, bddGraph.img(node10));
    }

    @Test
    public void testPreImgOfEmptyIsEmpty() {
        assertTrue(bddGraph.preImg(bddFactory.zero()).isZero());
    }

    @Test
    public void testPreImgOfNode01IsEmpty() {
        assertTrue(bddGraph.preImg(node01).isZero());
    }

    @Test
    public void testPreImgOfAllNodesIsAllNodesExcept11() {
        BDD allExcept11 = bddFactory.one().and(node11.not());
        assertEquals(allExcept11, bddGraph.preImg(bddFactory.one()));
    }

    @Test
    public void testPreImgOf00Is01And10() {
        BDD node01And10 = node01.or(node10);
        assertEquals(node01And10, bddGraph.preImg(node00));
    }

    @Test
    public void testGetNodesShouldReturnAllNodes() {
        assertEquals(bddFactory.one(), bddGraph.getNodes());
    }


}