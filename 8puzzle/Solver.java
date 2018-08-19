import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stack;

/**
 * For optimization we will include a number of things:
 * 1. Use 1 priority queue which contains both board and its twin
 * 2. Cache the computation of Manhattan priority as part of a node class
 */

public class Solver {
    private Node current;
    private boolean isSolvable;                  // default is false

    public Solver(Board initial){
        // find a solution to the initial board (using the A* algorithm)

        // throw error if passed null
        if (initial == null){
            throw new IllegalArgumentException("Null is an invalid argument");
        }

        // initialize variables
        MinPQ<Node> aPQ = new MinPQ<>();
        Node original = new Node(initial, null, 0);
        // generate twin in order to check if solvable
        Node twin = new Node(initial.twin(), null, 0);

        // adding original and twin nodes to PQ
        // include twin board in PQ because will eventually come up on the queue once the tings are too much
        aPQ.insert(original);
        aPQ.insert(twin);

        // set current node
        current = aPQ.min();

        // while the current board is not #goals, continue search based on the priority rankings
        while (!current.board.isGoal()){
            // store current as min and remove it from PQ
            current = aPQ.delMin();

            // add all of current node's neighbors to PQ
            for (Board board : current.board.neighbors()){

                // skip over neighbors which are the same as the predecessor board
                // as long as not first node where prior node is null
                if (current.previousNode != null && board.equals(current.previousNode.board)){
                    continue;
                }

                // create new node
                Node toAdd = new Node(board, current, current.nMoves + 1);

                // push node to PQ
                aPQ.insert(toAdd);
            }

            // set new current as board with minPQ => a bit redundant but its chill
            current = aPQ.min();
        }

        // once exiting the loop, determine whether the board or its twin was the solvable one
        // start from tail (which is current) and work backwards until reaching the head prior node is null
        Node leaf = current;

        while (leaf.previousNode != null){
            leaf = leaf.previousNode;
        }

        // check if head is OG board
        if (leaf.board == initial){
            isSolvable = true;
        }

        // else will remain false which is the default value
    }

    public boolean isSolvable(){
        // is the initial board solvable?
        return isSolvable;
    }

    public int moves(){
        // min number of moves to solve initial board; -1 if unsolvable
        if (!isSolvable()){
            return -1;
        }
        // else return number of moves

        return current.nMoves;
    }

    public Iterable<Board> solution(){
        // sequence of boards in a shortest solution; null if unsolvable

        // return null if unsolvable
        if (!isSolvable()){
            return null;
        }

        // we will use a stack so that when we print it out, we can pop from the 1st board to the last
        // this is because we are adding from the last board to the first board, so use the stack to reverse this
        // popping will enable to free up space
        Stack<Board> solution = new Stack<>();

        // push the tail leaf into the stack
        Node leaf = current;
        solution.push(leaf.board);

        // start a while loop going back to the head
        while (leaf.previousNode != null){
            // set leaf node to the previous node
            leaf = leaf.previousNode;

            // push board to ting
            solution.push(leaf.board);
        }

        // return solution
        return solution;
    }

    // create inner class for search node
    private class Node implements Comparable<Node>{

        private Board board;
        private Node previousNode;
        private int nMoves;
        private int priority;

        // instantiate search node
        // keep track of previous node, board, number of moves
        private Node(Board board, Node previousNode, int nMoves){
            this.board = board;
            this.previousNode = previousNode;
            this.nMoves = nMoves;
            priority = board.manhattan() + nMoves;
        }


        @Override
        // priority = manhattan + nMoves
        public int compareTo(Node that) {
            int thisPriority = this.priority;
            int thatPriority = that.priority;

            if (thisPriority > thatPriority){                               // if this is greater than that
                return 1;
            } else if (thisPriority < thatPriority){                        // if that is greater than this
                return -1;
            } else {                                                        // if that is equal to this
                return 0;
            }
        }
    }

    public static void main(String[] args) {


        // using terminal
        // create initial board from file
        int n = StdIn.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = StdIn.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }

        // using in app debugger
        // create initial board from file
//        In in = new In(args[0]);
//        int n = in.readInt();
//        int[][] blocks = new int[n][n];
//        for (int i = 0; i < n; i++)
//            for (int j = 0; j < n; j++)
//                blocks[i][j] = in.readInt();
//        Board initial = new Board(blocks);
//
//        // solve the puzzle
//        Solver solver = new Solver(initial);
//
//        // print solution to standard output
//        if (!solver.isSolvable())
//            StdOut.println("No solution possible");
//        else {
//            StdOut.println("Minimum number of moves = " + solver.moves());
//            for (Board board : solver.solution())
//                StdOut.println(board);
//        }
    }
}
