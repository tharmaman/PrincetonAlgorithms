import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * OPTIMIZATION
 * #TODO - need to create deluxeBFS
 * 1. Keep track of which array entries change, reuse the same arrays
 * #TODO - need to create deluxeBFS
 * 2. Run two breadth-first searches from v and w in lockstep
 * #DONE
 * 3. Cache recently computed length and ancestor queries
 */
public class SAP {
    private Digraph G;                                  // digraph to be used
    private int minLenInt;                              // recent min length query for ints
    private int minAncInt;                              // recent min ancestor query for ints
    private int minLenIter;                             // recent min length query for iterables
    private int minAncIter;                             // recent min ancestor query for iterables
    private int v;                                      // store recent vertex to check whether or not to recalculate
    private int w;                                      // store recent vertex to check whether or not to recalculate
    private Iterable<Integer> V;                        // store recent vertex to check whether or not to recalculate
    private Iterable<Integer> W;                        // store recent vertex to check whether or not to recalculate
    private boolean hasPath;                            // flag previous result as having a path or not

    // DONE
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {

        // handle corner cases
        if (G == null) {
            throw new IllegalArgumentException("Cannot accept null");
        }

        // initialize graph
        this.G = new Digraph(G);

        // initialize caches
        minLenInt = Integer.MAX_VALUE;                              // anything found will update this
        minAncInt = -1;                                             // anything found will update this
        minLenIter = Integer.MAX_VALUE;                             // anything found will update this
        minAncIter = -1;                                            // anything found will update this
        hasPath = false;                                            // setting has path to false
    }

    // #DONE
    // helper function to run the actual BFS and update variables
    // version which takes ints
    private void runBFS(int v, int w) {

        // handling corner cases
        if ((v < 0 || v >= G.V()) || (w < 0 || w >= G.V())) {
            throw new IllegalArgumentException("v or w is out of range");
        }
        if (v == w) {
            minAncInt = v;
            minLenInt = 0;
        }

        // make sure bfs is clean before proceeding
        cleanBFS(false);

        // store vertices in order to check the need for recalculation
        this.v = v;
        this.w = w;

        // creating BFS for both vertices v an w
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);

        // setting up flag to track if a path has been changed
        boolean flag = false;

        // loop through graph vertices
        // find vertices where v and w have a path to
        // check if a reduced length
        // store as new len
        for (int i = 0; i < G.V(); i++) {

            // check if both share a common ancestor
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {

                // calculate the length of the combined path
                int testLen = bfsV.distTo(i) + bfsW.distTo(i);

                // check if test length is the new minimum length
                if (testLen < minLenInt) {

                    // set flag to true
                    flag = true;

                    // update both caches
                    minLenInt = testLen;
                    minAncInt = i;
                }
            }
        }
        if (flag == true) {
            hasPath = true;
        }
    }

    // #DONE
    // helper function to run the actual BFS and update variables
    // version which takes iterables
    private void runBFS(Iterable<Integer> V, Iterable<Integer> W) {

        // handling corner cases
        if (V == null || W == null) {
            throw new IllegalArgumentException("Cannot accept null");
        }
        for (Integer i : V) {
            if (i == null) {
                throw new IllegalArgumentException("Iterable V contains null");
            }
            if (i < 0 || i >= G.V()) {
                throw new IllegalArgumentException("Iterable V contains a vertex out of bounds");
            }
        }
        for (Integer i : W) {
            if (i == null) {
                throw new IllegalArgumentException("Iterable W contains null");
            }
            if (i < 0 || i >= G.V()) {
                throw new IllegalArgumentException("Iterable W contains a vertex out of bounds");
            }
        }

        // make sure bfs is clean before proceeding
        cleanBFS(true);

        // store vertices in order to check the need for recalculation
        this.V = V;
        this.W = W;

        // creating BFS for both vertices v an w
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, V);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, W);

        // setting up flag to track if a path has been changed
        boolean flag = false;

        // loop through graph vertices
        // find vertices where v and w have a path to
        // check if a reduced length
        // store as new len
        for (int i = 0; i < G.V(); i++) {

            // check if both share a common ancestor
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {

                // calculate the length of the combined path
                int testLen = bfsV.distTo(i) + bfsW.distTo(i);

                // check if test length is the new minimum length
                if (testLen < minLenIter) {

                    // set flag to true
                    flag = true;

                    // update both caches
                    minLenIter = testLen;
                    minAncIter = i;
                }
            }
        }
        if (flag == true) {
            hasPath = true;
        }
    }

    // re-initialize BFS
    private void cleanBFS(boolean isIter) {

        // hasPath is reset regardless
        hasPath = false;

        // if iterable is being reset
        if (isIter) {
            minAncIter = -1;
            minLenIter = Integer.MAX_VALUE;

        // else if integer is being reset
        } else {
            minAncInt = -1;
            minLenInt = Integer.MAX_VALUE;
        }
    }

    // DONE
    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {

        // check if one of the vertices were different from the last
        // then run a new BFS
        // otherwise info will be the same as before
        if (this.v != v || this.w != w) {
            runBFS(v, w);
        }

        if (!hasPath) {
            minLenInt = -1;
        }

        return minLenInt;
    }

    // DONE
    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {

        // check if one of the vertices were different from the last
        // then run a new BFS
        // otherwise info will be the same as before
        if (this.v != v || this.w != w) {
            runBFS(v, w);
        }

        return minAncInt;
    }

    // DONE
    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {

        // handling corner cases
        if (this.V != v || this.W != w) {
            runBFS(v, w);
        }

        // if paths are not connected
        if (!hasPath) {
            return -1;
        }

        return minLenIter;
    }

    // DONE
    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (this.V != v || this.W != w) {
            runBFS(v, w);
        }
        return minAncIter;
    }


    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        System.out.println(sap.ancestor(3, 5));
        System.out.println(sap.length(3, 5));
//        System.out.println("Enter vertex v: ");
//        while (!StdIn.isEmpty()) {
//            int v = StdIn.readInt();
//            System.out.println("Enter vertex w: ");
//            int w = StdIn.readInt();
//            int length   = sap.length(v, w);
//            int ancestor = sap.ancestor(v, w);
//            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
//            System.out.println("\nEnter vertex v: ");
//        }
    }
}
