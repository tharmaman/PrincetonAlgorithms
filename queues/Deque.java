import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node first;                 // beginning of queue
    private Node last;                  // end of queue
    private int n;                      // number of elements on queue

    private class Node{
        // initializing private helper class
        private Item item;
        private Node next;
        private Node previous;              // needed to implement both pop and dequeue
    }

    public Deque(){
        // initializing empty deque
        first = null;
        last = null;
        n = 0;
    }

    public boolean isEmpty(){
        // is the deque empty?
        return n == 0;
    }

    public int size(){
        // return the number of items on the deque
        return n;
    }

    public void addFirst(Item item){
        // add item to the beginning
        if (item == null){
            throw new IllegalArgumentException("Cannot accept null");
        }
        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;
        first.previous = null;
        if (isEmpty()){
            last = first;
        } else {
            oldFirst.previous = first;
        }
        n++;
    }

    public void addLast(Item item){
        // add item to the end
        if (item == null){
            throw new IllegalArgumentException("Cannot accept null");
        }
        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        last.previous = oldLast;
        if (isEmpty()){
            first = last;
        } else {
            oldLast.next = last;
        }
        n++;
    }

    public Item removeFirst(){
        // remove and return the item from the front
        if (isEmpty()){
            throw new NoSuchElementException("Deque is empty!");
        }
        Item oldFirstItem = first.item;          // store old first item
        if (first.next != null){                 // as long as next value is not null
            first = first.next;                  // set first node to reference the next node
        }
        first.previous = null;                   // set first previous to null
        n--;                                     // decrement counter
        if (isEmpty()) {                         // can't make a move from null to null
            last = null;                         // set last node to null as well;
            first = null;                        // set first to null, because can't point to previous null
        }
        return oldFirstItem;                        // return first item

    }

    public Item removeLast(){
        // remove and return the item from the end
        if (isEmpty()){
            throw new NoSuchElementException("Deque is empty!");
        }
        Item oldLastItem = last.item;           // store old last item
        if (last.previous != null){             // check if last previous is not null
            last = last.previous;               // set last to previous
        }
        last.next = null;                       // set last next to null
        n--;                                    // decrement counter
        if(isEmpty()){                          // if list is empty, last node would be null automatically
            first = null;                       // set the first node to null as well;
            last = null;                        // set last to null, because otherwise null pointer exception
        }
        return oldLastItem;
    }

    public Iterator<Item> iterator(){
        /* return iterator that iterates over items from beg to end */
        return new ListIterator();
    }

    private class ListIterator implements Iterator<Item>{
        private Node current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Risky move!");
        }

        @Override
        public Item next() {
            if(!hasNext()){
                throw new NoSuchElementException("No more items in list");
            }
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    public static void main(String[] args) {
        // allow for unit testing

        System.out.println("Initializing Deque...");

        Deque<Integer> deque = new Deque<>();

        deque.addLast(1);
        deque.addLast(2);
        deque.addFirst(0);

        for (Integer i : deque){
            System.out.println(i);
        }

        System.out.println("Now popping tings");

        System.out.println(deque.removeFirst());
        System.out.println(deque.removeFirst());
        System.out.println(deque.removeFirst());

    }
}
