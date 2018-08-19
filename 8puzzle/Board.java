import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdRandom;

/**
 * For optimization we will include a number of things:
 * 1. Use a 1d array instead of 2d array
 * 2. Cache Manhattan distance of board instead of constantly recalculating
 * 3. Use a char array instead of ints
 */

public class Board {
    private final int n;
    private final char[] newBlocks;
    private int manhattan;
    private int zero;


    public Board(int[][] blocks){
        // construct a board from an n-by-n array of blocks

        // throw error if passed null
        if (blocks == null){
            throw new IllegalArgumentException("Null is an invalid argument");
        }

        // initialize variables
        manhattan = 0;
        zero = 0;
        n = blocks.length;

        // creating an 1D array using the n-by-n dimensions
        newBlocks = new char[n * n];


        // loop through newBlocks
        // 1. store blocks in new blocks as a CHAR
        // 2. add to manhattan distance

        for (int i = 0; i < n * n; i++){

            // cast integer to char to save space
            newBlocks[i] = (char) blocks[i / n][i % n];

            // check if encountered the 0 block
            // continue through iterating and don't calculate manhattan distance
            if (newBlocks[i] == 0){
                // store index of zero value
                zero = i;
                continue;
            }

            // calculate values to manhattan distance
            // use absolute values to accounts for left and down
            // i is target position and newBlocks[i] is current position
            // got to add the -1 to adjust for index form in order to push
            // seeing as if 1 should be at index 0
            // zero to the last index
            int manY = Math.abs(i/n - (newBlocks[i]-1)/n);
            int manX = Math.abs(i%n - (newBlocks[i]-1)%n);

            // if out of place meaning manV + manH != 0
            // add to nHam counter
            manhattan += (manY + manX);
        }
    }

    public int dimension(){
        // board dimension n
        return n;
    }

    public int hamming(){
        // number of blocks out of place
        int nHam = 0;
        for (int i = 0; i < newBlocks.length; i++){
            if (newBlocks[i] != (i + 1) && newBlocks[i] != 0){
                nHam++;
            }
        }
        return nHam;
    }

    public int manhattan(){
        // sum of Manhattan distances between blocks and goal
        return manhattan;
    }

    public boolean isGoal(){
        // is this board the goal board?
        // check if there are no blocks out of place
        return manhattan == 0;
    }

    public Board twin(){
        // a board that is obtained by exchanging any pair of blocks
        // zero stays the same
        // use only one twin in solver
        // swap one pair only
        char[] twin = copyBlocks(newBlocks);
        // declaring index to be swapped
        // setting i = zero in order to start the while loop
        int i = zero;

        // just for simplicity we will only swap indexes next to each other
        // randomly swap i and i + 1
        // can only swap
        // as long as either twin[i] or twin[i+1] do not reference the zero
        // we subtract -1 from twin.length since it is not inclusive
        // that's why we will subtract 1 from the ending range of the Uniform distribution

        while (twin[i] == 0 || twin[i + 1] == 0){
            i = StdRandom.uniform(0, twin.length - 1);
        }
        exch(twin, i, i + 1);

        // return a new board created from the twin
        return new Board(to2D(twin));
    }

    public boolean equals(Object y){
        // does this board equal y?

        // creating corner cases
        if (y == this){
            return true;                            // same object
        }
        if (y == null){
            return false;                           // handle null
        }
        if (y.getClass() != this.getClass()){
            return false;                           // not same class
        }

        // cast y as a board object once same class
        Board that = (Board) y;

        // Handle boards of different sizes
        if (this.n != that.n){
            return false;
        }

        // compare tings
        return this.newBlocks == that.newBlocks;
    }

    public Iterable<Board> neighbors(){

        // all neighboring boards
        Stack<Board> neighbors = new Stack<>();

        // initialize values for zero
        int zeroY = zero / n;
        int zeroX = zero % n;

        // 0 <= zero < n
        // for both row and column

        // zero moves up (y - 1)
        if ((zeroY - 1) >= 0){
            neighbors.push(createNeighbor(zero - n));
        }

        // zero moves down (y + 1)
        if ((zeroY + 1) < n){
            neighbors.push(createNeighbor(zero + n));
        }

        // zero moves left (x - 1)
        if ((zeroX - 1) >= 0){
            neighbors.push(createNeighbor(zero - 1));
        }

        // zero moves right (x + 1)
        if ((zeroX + 1) < n){
            neighbors.push(createNeighbor(zero + 1));
        }

        // return neighbor stack
        return neighbors;
    }

    /******************************************************************
     *
     * HELPER FUNCTIONS
     *
     ******************************************************************/

    // need to convert 1D char array to 2D int array in order to instantiate board
    private int[][] to2D(char[] arrIn){

        // setting up variables
        int r;                                      // row
        int c;                                      // column
        int [][] arrOut = new int[n][n];            // output array

        for (int i = 0; i < arrIn.length; i++){
            r = i / n;
            c = i % n;
            arrOut[r][c] = arrIn[i];
        }
        return arrOut;
    }

    // exchanging positions within a 1D array
    private void exch(char[] arr, int i, int j){
        char temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // exchanging positions within a 2D array
    private void exch(int[][] arr, int i, int j){
        int temp = arr[i / n][i % n];
        arr[i / n][i % n] = arr[j / n][j % n];
        arr[j / n][j % n] = temp;
    }

    // creating a neighbor board
    private Board createNeighbor(int nextZero){
        int[][] tmp = to2D(newBlocks);
        exch(tmp, zero, nextZero);
        return new Board(tmp);
    }

    private char[] copyBlocks(char[] arrIn){
        char[] arrOut = new char[arrIn.length];
        for (int i = 0; i < arrIn.length; i++){
            arrOut[i] = arrIn[i];
        }
        return arrOut;
    }

    // override for to string representation of the board
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n).append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", (int) newBlocks[i*n + j]));
            }
            s.append("\n");
        }
        return s.toString();
    }


    public static void main(String[] args) {
        // for unit testing (not graded)

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        System.out.println(initial.hamming());
    }
}
