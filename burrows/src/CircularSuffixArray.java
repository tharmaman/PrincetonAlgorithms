import edu.princeton.cs.algs4.SuffixArrayX;

public class CircularSuffixArray {
    private SuffixArrayX suffixArray;
    private String s;

    /**
     * #DONE
     * @param s circular suffix array base
     */
    public CircularSuffixArray(String s) {

        // handling corner cases
        if (s == null) {
            throw new IllegalArgumentException("Cannot accept null");
        }

        // initialize variables
        this.s = s;
        suffixArray = new SuffixArrayX(s);
    }

    /**
     * #DONE
     * @return length of s
     */
    public int length() {
        return s.length();
    }

    /**
     * #DONE
     * @param i sorted suffix
     * @return
     */
    public int index(int i) {

        // handling corner cases
        if (i < 0 || i >= suffixArray.length()) {
            throw new IllegalArgumentException("Index is out of bounds");
        }

        return suffixArray.index(i);
    }

    /**
     * #TODO
     * unit testing (required)
     * @param args
     */
    public static void main(String[] args) {
        String s = "ARD!RCAAAABB";

        CircularSuffixArray test = new CircularSuffixArray(s);


        for (int i = 0; i < s.length(); i++) {
            System.out.println(s.charAt(test.index(i)));
        }
    }

}
