import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    // think of a bag where the removal is random but order can be oldest -> newest or vice versa because doesn't matter

    private Item[] q;                       // queue elements
    private int n;                          // number of elements on queue



    public RandomizedQueue(){
        // construct an empty randomized queue
        q = (Item[]) new Object[2];
        n = 0;
    }

    private void resize(int capacity){
        // implement PRIVATE resizing function
        Item[] temp = (Item[]) new Object[capacity];
        for (int i = 0; i < n; i++){
            temp[i] = q[i];
        }
        q = temp;
    }

    public boolean isEmpty(){
        // is the randomized queue empty?
        return n == 0;
    }

    public int size(){
        // return the number of items on the deque
        return n;
    }

    public void enqueue(Item item){
        // add the item
        if (item == null){
            throw new IllegalArgumentException("Cannot add null to the queue");
        }
        if (n == q.length){                                      // remember this is elements not indices
            resize(2 * q.length);                       // double size of array if full (len
        }
        q[n] = item;                                            // create new space for item b/c n = i + 1
        n++;                                                    // increment elements
    }

    public Item dequeue(){
        // #TODO
        // remove and return a random item
        // if middle values are set to null, then iterator would not work
        // need to think of a way to account for those middle null values
        // since its a randomized queue, basically order does not matter since stack vs queue depends on whats removed
        // can replace the remove element with either the first or last element, can just do it at the end like a stack

        if (isEmpty()){
            throw new NoSuchElementException("Array is empty!");
        }
        int random = StdRandom.uniform(0, n);                        // generate random integer between 0 (inclusive and n (exclusive)
        Item item = q[random];                                          // store random item for return
        if (random != n - 1){                                           // if index is not the last index in the array
           q[random] = q[n - 1];                                        // overwrite removed item with last item
        }
        q[n - 1] = null;                                                // q[random] could be q[n-1] and this would work
        n--;                                                            // decrement element counter
        if(n > 0 && n == q.length/4){                                   // only halve array once count is a quarter of the full array
            resize(q.length/2);                                // allows for constant amortized time
        }

        return item;                                                    // return the removed item
    }

    public Item sample(){
        // return a random item (but do not remove it)
        if (isEmpty()){
            throw new NoSuchElementException("Array is empty!");
        }
        int random = StdRandom.uniform(0, n);                      // generate random integer between 0 (inclusive and n (exclusive)
        Item item = q[random];                                        // create item object and store random point in array
        return item;                                                  // return sample
    }

    public Iterator<Item> iterator(){
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator{
        private int i = 0;


        @Override
        public boolean hasNext() {
            return i < n;                                       // once i = n, reached end of array
        }

        @Override
        public Object next() {
            if(!hasNext()){
                throw new NoSuchElementException("Reached end of array");
            }
            Item item = q[i];                                   // no wrap necessary since just adding to the end
            i++;                                                // increment counter
            return item;                                        // return current item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Risky Move");
        }
    }

    public static void main(String[] args) {
        // allow for unit testing
        System.out.println("Initializing Randomized queue...");

        RandomizedQueue<Integer> random = new RandomizedQueue<>();

        System.out.println(random.isEmpty());
        System.out.println(random.size());

        System.out.println("Adding elements");

        random.enqueue(1);
        random.enqueue(2);
        random.enqueue(3);
        random.enqueue(4);
        random.enqueue(5);

        System.out.println("Popping elements");

        System.out.println(random.dequeue());

        System.out.println("Sampling");

        System.out.println(random.sample());

        System.out.println("looping...");

        for (Integer i : random){
            System.out.println(i);
        }
    }

}
