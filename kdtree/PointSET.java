import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;

public class PointSET {
    private SET<Point2D> set;                   // going to use pre-made set

    /**
     * #DONE
     * Construct an empty set of points
     */
    public PointSET(){
        set = new SET<>();
    }

    /**
     * #DONE
     * is the set empty?
     * @return true if the set is empty, false otherwise
     */
    public boolean isEmpty(){
        return set.isEmpty();
    }

    /**
     * #DONE
     * number of points in the set
     * @return as above
     */
    public int size(){
        return set.size();
    }

    /**
     * #DONE
     * add the point to the set (if it is not already in the set)
     * @param p the point to be added
     */
    public void insert(Point2D p){

        // handling corner cases
        if (p == null){
            throw new IllegalArgumentException("Cannot accept null as an argument");
        }

        // add directly to set if not already present
        set.add(p);
    }

    /**
     * #DONE
     * does the set contain point p?
     * @param p the point to be checked
     * @return true if the set contains point p, false otherwise
     */
    public boolean contains(Point2D p){

        // handling corner case
        if (p == null){
            throw new IllegalArgumentException("Cannot accept null as an argument");
        }

        return set.contains(p);
    }

    /**
     * #DONE
     * draw all points to standard draw
     */
    public void draw(){
        for (Point2D p: set){
            p.draw();
        }
    }

    /**
     * #DONE
     * all points that are inside the rectangle (or on the boundary)
     * @param rect a rectangle used to scan for points
     * @return an stack or linked list with all points inside the rectangle (or on the boundary)
     */
    public Iterable<Point2D> range(RectHV rect){

        // handling corner case
        if (rect == null){
            throw new IllegalArgumentException("Cannot accept null as an argument");
        }

        // using a brute force method
        Stack<Point2D> found = new Stack<>();

        // loop through set points to see if contained in rectangle
        for (Point2D p : set){
            if (rect.contains(p)){
                // push to stack
                found.push(p);
            }
        }

        return found;
    }

    /**
     * #DONE
     * a nearest neighbor in the set to point p
     * @param p the point to be checked
     * @return a 2D-Point which is nearest to point p, null if the set is empty
     */
    public Point2D nearest(Point2D p){

        // handling corner cases
        if (p == null){
            throw new IllegalArgumentException("Cannot accept null as an argument");
        }
        if (isEmpty()){
            return null;
        }

        // initializing nearest variables
        Point2D championPoint = null;
        double championDistance = Double.MAX_VALUE;              // setting max as first point's distance will be lower than this

        // loop through set
        for (Point2D currentPoint : set){
            double currentDistance = p.distanceTo(currentPoint);
            // check if found a new minimum distance
            if (currentDistance < championDistance){

                // set as new nearest point & distance
                championPoint = currentPoint;
                championDistance = currentDistance;
            }
        }

        return championPoint;
    }

    public static void main(String[] args) {
        // unit testing of the methods (optional)
        Point2D a = new Point2D(2, 3);
        Point2D b = new Point2D(1, 13);
        Point2D c = new Point2D(4, 2);
        Point2D d = new Point2D(6, 1);
        Point2D e = new Point2D(20, 8);
        Point2D f = new Point2D(5, 7);

        PointSET lit = new PointSET();
        lit.insert(a);
        lit.insert(b);
        lit.insert(c);
        lit.insert(d);
        lit.insert(e);
        lit.insert(f);
    }
}
