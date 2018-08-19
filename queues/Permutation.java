import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    // Takes integer k as a command-line argument
    // reads in a sequence of strings from another standard input using StdIn.readString()
    // prints out exactly k of each string, UNIFORMLY AT RANDOM
    // implement using RandomizedQueue

    public static void main(String[] args) {
        System.out.println("Initializing permutation...");
        RandomizedQueue<String> randomPerms = new RandomizedQueue<>();

        // argument entered from command line
        int k = Integer.parseInt(args[0]);

        // read input of string file
        while (!StdIn.isEmpty()){
            String item = StdIn.readString();                   // store each string from file as an item
            randomPerms.enqueue(item);                          // add each string to queue
        }

        // add throw exception as k needs to be less than the amount of Strings in file
        if (k > randomPerms.size()){
            throw new IllegalArgumentException("K is larger than the amount of Strings on file");
        }

        // for loop to print out loop exactly K times
        for (int i = 0; i < k; i++){
            StdOut.println(randomPerms.dequeue());                // dequeue to pop out strings so redundancy doesn't occur
        }
    }
}
