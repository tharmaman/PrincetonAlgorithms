import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;

public class MoveToFront {
    private static final int R = 256;
    private static final int CHAR = 8;              // also known as lgR


    /**
     * #DONE
     * apply move-to-front encoding,
     * reading from standard input and
     * writing to standard output
     */
    public static void encode() {

        // create a java util linked list for alphabet because has more functionality
        LinkedList<Integer> alphabetLinked = new LinkedList();

        // loop and fill out linked list with ASCII characters (to 256)
        for (int i = 0; i < R; i++) {
            alphabetLinked.add(i);
        }

        // iterate through scanner while not empty
        while (!BinaryStdIn.isEmpty()) {

            // read in
            int in = BinaryStdIn.readChar();

            // store out
            int out = alphabetLinked.indexOf(in);

            // char automatically converts to ASCII int
            // write out ASCII value to character
            BinaryStdOut.write(out, CHAR);

            // remove current in position in alphabet
            alphabetLinked.remove(out);

            // reattach in to the front
            alphabetLinked.addFirst(in);
        }

        // close scanner
        BinaryStdOut.close();
    }

    /**
     * #DONE
     * apply move-to-front decoding,
     * reading from standard input and
     * writing to standard output
     */
    public static void decode() {
        // create a java util linked list for alphabet because has more functionality
        LinkedList<Integer> alphabetLinked = new LinkedList();

        // loop and fill out linked list with ASCII characters (to 256)
        for (int i = 0; i < R; i++) {
            alphabetLinked.add(i);
        }

        // iterate through scanner while not empty
        while (!BinaryStdIn.isEmpty()) {

            // read in
            int in = BinaryStdIn.readChar();

            // store out
            // the ith character
            int out = alphabetLinked.get(in);

            // char automatically converts to ASCII int
            // write out the ith character in the sequence
            BinaryStdOut.write(out, CHAR);

            // remove current in position in alphabet
            alphabetLinked.remove(in);

            // reattach out to the front
            alphabetLinked.addFirst(out);
        }

        // close scanner
        BinaryStdOut.close();
    }

    /**
     * #DONE
     * @param args
     * if args[0] is '-', apply move-to-front encoding
     * if args[0] is '+', apply move-to-front decoding
     */
    public static void main(String[] args) {
        // corner case if nothing was entered
        if (args.length == 0) {
            throw new IllegalArgumentException("'-' to apply encode or '+' to apply decode");
        }

        if (args[0].equals("-")) {
            encode();
        }
        else if (args[0].equals("+")) {
            decode();

            // handle corner case if was entered
        } else {
            throw new IllegalArgumentException("'-' to apply encode or '+' to apply decode");
        }
    }
}
