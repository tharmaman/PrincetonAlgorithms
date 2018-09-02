import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.LinearProbingHashST;
import edu.princeton.cs.algs4.Queue;

public class BurrowsWheeler {
    private static final int CHAR = 8;                  // also known as lgR

    /**
     * #DONE
     * apply Burrows-Wheeler transform,
     * reading from standard input and writing
     * to standard output
     */
    public static void transform() {

        // read in string
        String s = BinaryStdIn.readString();

        // create circular array
        CircularSuffixArray cArray = new CircularSuffixArray(s);

        /*
        #TODO
        can optimize by checking if first char of String is closer to beginning or end of ASCII
        Decrement loop if closer to the end
        Increment loop if otherwise
         */

        // loop through array to write out first row
        for (int i = 0; i < cArray.length(); i++) {

            // mark first as the OG string array
            if (cArray.index(i) == 0) {
                // write out as first;
                BinaryStdOut.write(i);
                // break from loop
                break;
            }
        }

        // do second loop to print last column
        for (int i = 0; i < cArray.length(); i++) {

            // grab sorted array index
            int cIndex = cArray.index(i);

            // start at sorted index, then add length - 1 of string to find last
            // mod it to wrap around the string
            int output = s.charAt((cIndex + s.length() - 1) % s.length());

            // write output as a Char (8 bits)
            BinaryStdOut.write(output, CHAR);
        }

        // close scanner
        BinaryStdOut.close();
    }

    /**
     * #DONE
     * apply Burrows-Wheeler inverse transform,
     * reading from standard input and writing
     * to standard output
     */
    public static void inverseTransform() {

        // read in text
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();

        // initialize arrays
        char[] t = s.toCharArray();
        int[] next = new int[t.length];

        // create circular array
        CircularSuffixArray sorted = new CircularSuffixArray(s);

        // create reference list using a Queue since earliest duplicates since if i < j, then next[i] < next[j]
        LinearProbingHashST <Character, Queue<Integer>> charToSorted = new LinearProbingHashST<>();
//        LinearProbingHashST <Character, Integer> charCount = new LinearProbingHashST<>();

        // loop through sorted array
        for (int i = 0; i < t.length; i++) {

            // create a new key if not in hash table
            if (!charToSorted.contains(t[i])) {
                charToSorted.put(t[i], new Queue<>());
//                charCount.put(t[i], 1);
            }

            // enqueue reference
            charToSorted.get(t[i]).enqueue(i);
            // grab and update counter
//            int counter = charCount.get(t[i]);
//            int updateCount = counter + 1;
//            charCount.put(t[i], updateCount);
        }

        // iterate through sorted array
        for (int i = 0; i < sorted.length(); i++) {

            // store first index as characters
            char sortedChar = t[sorted.index(i)];

            // dequeue character index
            next[i] = charToSorted.get(sortedChar).dequeue();
        }

        // iterate through count of next array but follow the next paths
        for (int i = 0, currentRow = first; i < next.length; i++, currentRow = next[currentRow]) {

            // grab character
            char outputChar = t[sorted.index(currentRow)];

            // write output
            BinaryStdOut.write(outputChar);
        }
        // close scanner
        BinaryStdOut.close();
    }

    // #DONE
    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {

        // corner case if nothing was entered
        if (args.length == 0) {
            throw new IllegalArgumentException("'-' to apply transform or '+' to apply inverse transform");
        }

        if (args[0].equals("-")) {
            transform();
        }
        else if (args[0].equals("+")) {
            inverseTransform();

        // handle corner case if was entered
        } else {
            throw new IllegalArgumentException("'-' to apply transform or '+' to apply inverse transform");
        }
    }
}
