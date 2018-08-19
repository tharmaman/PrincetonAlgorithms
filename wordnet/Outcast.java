import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet wordNet;                        // take WordNet object

    // #DONE
    // constructor takes a WordNet object
    public Outcast(WordNet wordNet) {

        // handling corner cases
        if (wordNet == null) {
            throw new IllegalArgumentException("Cannot accept null");
        }

        this.wordNet = wordNet;
    }

    // #DONE
    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {

        // handling corner cases
        if (nouns == null) {
            throw new IllegalArgumentException("Cannot accept null");
        }

        // initialize variables
        int outcastDist = 0;
        String outcastNoun = "";

        // loop through nouns
        for (String testNoun : nouns) {
            int outcastTest = 0;
            for (String checkNoun : nouns) {
                outcastTest += wordNet.distance(testNoun, checkNoun);
            }

            // check if new max
            if (outcastTest > outcastDist) {
                outcastDist = outcastTest;
                outcastNoun = testNoun;
            }
        }
        return outcastNoun;
    }

    // unit testing
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
