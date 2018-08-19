import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinearProbingHashST;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycleX;
import edu.princeton.cs.algs4.StdOut;


public class WordNet {

    private SET<String> nounsList;                                   // iterable list of nouns
    private DirectedCycleX dirCycle;                                 // used to check if new graph is a DAG
    private Digraph G;                                               // digraph to be used
    private SAP sap;                                                 // shortest ancestral path client
    private LinearProbingHashST<String, Bag<Integer>> nounLookup;    // hash table to lookup nouns and their int position, nouns may have multiple ids
    private LinearProbingHashST<Integer, String> synsetLookup;       // hash table to lookup synID and their synset

    // #DONE
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {

        // handling corner cases
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("Cannot accept null");
        }

        // initialize instance variables
        nounsList = new SET<>();
        nounLookup = new LinearProbingHashST<>();
        synsetLookup = new LinearProbingHashST<>();

        // handle input files
        In synIn = new In(synsets);
        In hypIn = new In(hypernyms);

        // initialize counters
        int synCounter = 0;

        // parse synsets line by line
        while (synIn.hasNextLine()) {

            // Field 1 = synset id
            // Field 2 = synonym set
            // Field 3 = dictionary definition (gloss)
            synCounter++;

            // can be split by comma
            String line[] = synIn.readLine().split(",");

            // store id
            int id = Integer.parseInt(line[0]);
            // store synset
            String set = line[1];

            // store set in synset hash table
            synsetLookup.put(id, set);

            // split sets for noun lookup
            String nouns[] = set.split(" ");

            // add to noun hash table if new noun
            for (String noun: nouns) {

                // if new noun
                if (!nounsList.contains(noun)) {

                    // add to noun set
                    nounsList.add(noun);

                    // create a bag to hold noun IDs
                    Bag<Integer> ids = new Bag<>();
                    ids.add(id);

                    // add to noun hash table with ids bag
                    nounLookup.put(noun, ids);
                }

                // if not new noun
                else {

                    // just add id to noun hash table ids bag
                    nounLookup.get(noun).add(id);
                }
            }
        }

        // initialize graph
        G = new Digraph(synCounter);

        // parse hypernyms line by line
        while (hypIn.hasNextLine()) {


            // Field 1 = vertex
            // Field n = connecting vertex
            // can be split by comma
            String line[] = hypIn.readLine().split(",");

            // store primary vertex
            int vertex = Integer.parseInt(line[0]);

            // loop through remaining elements of line array
            for (int i = 1; i < line.length; i++) {

                // store temp connecting vertex variable
                int connectVertex = Integer.parseInt(line[i]);

                // add edge to graph
                G.addEdge(vertex,connectVertex);
            }
        }

        // corner case
        // check if input is not a DAG
        dirCycle = new DirectedCycleX(G);

        if (dirCycle.hasCycle()){
            throw new IllegalArgumentException("Input is not a DAG");
        }

        // check for more than 1 roots
        // basically iterate through vertices
        // if the there are no vertices to v by edges pointing from v
        // i.e. the adj bag is empty
        // then that vertex is a root
        int rootCounter = 0;

        // loop through graph vertices
        for (int i = 0; i < G.V(); i++) {
            int adjCounter = 0;

            // store vertex as adj bag
            Iterable<Integer> adjBag = G.adj(i);

            for (int adj: G.adj(i)) {
                // count elements from adj bag
                adjCounter++;
            }

            // check if the adj bag is empty
            if (adjCounter == 0) {

                // add as a root if empty
                rootCounter++;

                // if there is not EXACTLY one root, then graph is not rooted
                if(rootCounter != 1) {
                    throw new IllegalArgumentException("Input does not have exactly one root");
                }
            }
        }

        // if all good, initialize new SAP
        sap = new SAP(G);
    }

    // #DONE
    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounsList;
    }

    // #DONE
    // is the word a WordNet noun?
    public boolean isNoun(String word) {

        // handling corner cases
        if (word == null) {
            throw new IllegalArgumentException("Cannot accept null");
        }

        return nounsList.contains(word);
    }

    // #DONE
    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {

        // handling corner cases
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("Either nounA or nounB is null");
        }
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Either nounA or nounB is not a WordNet noun");
        }

        Bag<Integer> nounAInts = nounLookup.get(nounA);
        Bag<Integer> nounBInts = nounLookup.get(nounB);

        return sap.length(nounAInts, nounBInts);
    }

    // #DONE
    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {

        // handling corner cases
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("Either nounA or nounB is null");
        }
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Either nounA or nounB is not a WordNet noun");
        }

        Bag<Integer> nounAInts = nounLookup.get(nounA);
        Bag<Integer> nounBInts = nounLookup.get(nounB);

        int ancestor = sap.ancestor(nounAInts, nounBInts);

        if (ancestor == -1) {
            return "No Common Ancestor";
        }

        return synsetLookup.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);

        StdOut.println(wordnet.isNoun("infectious_disease"));
        StdOut.println(wordnet.sap("AIDS", "immunodeficiency"));
        StdOut.println();
    }
}
