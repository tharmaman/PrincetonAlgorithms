import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;

/**
 *
 * OPTIMIZATION
 * #DONE
 * 1. Squared Distances
 *      - avoids expensive operation of taking square root
 *
 * #DONE
 * 2. Range Search
 *      a) if query rectangle intersects splitting line segment
 *          - then recursively search BOTH subtrees
 *      b) else
 *          - recursively search the ONE subtree where intersection points could be
 *
 */

public class KdTree {
    private Node root;
    private int size;
    private final boolean VERTICAL_SPLIT = false;
    private final boolean HORIZONTAL_SPLIT = true;

    /**
     * #DONE
     * Create an inner class for node
     */

    private class Node {
        private Point2D p;              // the point
        private RectHV rect;            // the axis-aligned rectangle corresponding to this node
        private Node lb;                // the left/bottom subtree
        private Node rt;                // the right/top subtree

        // initializer for Node
        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }

    /**
     * #DONE
     * Construct an empty set of points
     */
    public KdTree(){
        root = null;
        size = 0;
    }

    /**
     * #DONE
     * is the set empty?
     * @return true if the set is empty, false otherwise
     */
    public boolean isEmpty(){
        return size == 0;
    }

    /**
     * #DONE
     * number of points in the set
     * @return as above
     */
    public int size(){
        return size;
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

        // recursively go through tree and place new point and reconnect the nodes
        root = insert(root, p, VERTICAL_SPLIT, 0, 0, 1, 1);
    }

    /**
     * #DONE
     * helper function to assist with the recursive insert
     * @param n node to be inserted
     * @param p 2D point to act as a key
     * @param orientation either VERTICAL_SPLIT or HORIZONTAL_SPLIT
     * @param xMin of 2D axis aligned rectangle
     * @param yMin of 2D axis aligned rectangle
     * @param xMax of 2D axis aligned rectangle
     * @param yMax of 2D axis aligned rectangle
     * @return a new node
     */
    private Node insert(Node n, Point2D p, boolean orientation, double xMin, double yMin, double xMax, double yMax){

        // if added point is root
        if (n == null){
            size++;
            return new Node(p, new RectHV(xMin, yMin, xMax, yMax));
        }

        // saving partition point
        Point2D partition = n.p;

        // if partition had a VERTICAL SPLIT
        // compare x range
        if (orientation == VERTICAL_SPLIT){

            // go left of partition line
            if (p.x() < partition.x()){
                // adjust x-max for this partition
                xMax = partition.x();
                // create a left subtree
                n.lb = insert(n.lb, p, HORIZONTAL_SPLIT, xMin, yMin, xMax, yMax);

            // go right of partition line
            } else if (p.x() > partition.x()){
                // adjust x-min for this partition
                xMin = partition.x();
                // create a right subtree
                n.rt = insert(n.rt, p, HORIZONTAL_SPLIT, xMin, yMin, xMax, yMax);

            // lies on the partition line
            } else {
                // use same dimensions
                // create a right subtree
                n.rt = insert(n.rt, p, HORIZONTAL_SPLIT, xMin, yMin, xMax, yMax);
            }

        // if partition had a HORIZONTAL SPLIT
        // compare y range
        } else if (orientation == HORIZONTAL_SPLIT) {

            // go below the partition line
            if (p.y() < partition.y()) {
                // adjust y-max for this partition
                yMax = partition.y();
                // create a left subtree
                n.lb = insert(n.lb, p, VERTICAL_SPLIT, xMin, yMin, xMax, yMax);

                // go above the partition line
            } else if (p.y() > partition.y()) {
                // adjust y-min for this partition
                yMin = partition.y();
                // create a right subtree
                n.rt = insert(n.rt, p, VERTICAL_SPLIT, xMin, yMin, xMax, yMax);

                // if lies on the partition line
            } else {
                // use same dimensions
                // create a right subtree
                n.rt = insert(n.rt, p, VERTICAL_SPLIT, xMin, yMin, xMax, yMax);
            }
        }

        // return the modified node
        return n;
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

        // recursively search through tree, start at root
        return contains(root, p, VERTICAL_SPLIT);
    }

    /**
     * #DONE
     * Helper function for contains
     * @param n the node to be descended from
     * @param p the point to be checked
     * @param orientation whether VERTICAL or HORIZONTAL split
     * @return a boolean value denoting true if the tree contains p
     */
    private boolean contains(Node n, Point2D p, boolean orientation){

        // setting base case once reaching bottom of tree
        if (n == null){
            return false;
        }

        // if the point has already been found
        if (n.p.equals(p)){
            return true;
        }

        // storing comparable point
        double comp;

        // check orientations
        // starting from VERTICAL_SPLIT
        if (orientation == VERTICAL_SPLIT){
            // check the given points direction the Node's partition line
            comp = p.x() - n.p.x();

        // otherwise HORIZONTAL_SPLIT
        } else {
            comp = p.y() - n.p.y();
        }

        // if negative go left of tree
        if (comp < 0){
            return contains(n.lb, p, !orientation);

        // if positive OR on partition, go right of tree
        } else {
            return contains(n.rt, p, !orientation);
        }
    }


    /**
     * #DONE
     * draw all points to standard draw with partition lines
     */
    public void draw(){
        draw(root, VERTICAL_SPLIT);
    }

    /**
     * #DONE
     * Helper function to draw points with partition lines
     * @param n node that is to be drawn
     * @param orientation HORIZONTAL or VERTICAL split
     */
    private void draw(Node n, boolean orientation){

        // setting base case once reaching bottom of the tree
        if (n == null){
            return;
        }

        // traverse the left side of nodes
        draw(n.lb, !orientation);

        // draw the current node
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        n.p.draw();

        // draw the partition line
        StdDraw.setPenRadius();
        if (orientation == VERTICAL_SPLIT){
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(n.p.x(), n.rect.ymin(), n.p.x(), n.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.p.y());
        }

        // traverse the right side of nodes
        draw(n.rt, !orientation);
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

        // set up data structure
        Stack<Point2D> found = new Stack<>();

        // recursively search through tree
        range(root, rect, found);

        // return the list of found points
        return found;
    }

    /**
     * #DONE
     * helper function to check if in rectangle
     * @param n node to be checked
     * @param rect a rectangle used to scan for points
     * @param stack structure to store found points
     */
    private void range(Node n, RectHV rect, Stack<Point2D> stack){

        // if there are no nodes left
        if (n == null){
            return;
        }

        // query rectangle does not intersect segment then return
        // including nested intersections
        if (!n.rect.intersects(rect)){
            return;
        }

        // if query rectangle intersects splitting line segment
        if (rect.contains(n.p)){
            stack.push(n.p);
        }

        // if both subtrees intersect then search both subtrees

        // if intersects left rect
        if (n.lb != null && n.lb.rect.intersects(rect)) {
            // just search left trees
            range(n.lb, rect, stack);
        }

        // else only intersects right rect
        if (n.rt != null && n.rt.rect.intersects(rect)) {
            // just search right trees
            // check right trees
            range(n.rt, rect, stack);
        }
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

        // use MAX_VALUE because recursive query will definitely be less
        return nearest(root, p, Double.MAX_VALUE, root.p, VERTICAL_SPLIT);
    }

    /**
     * #DONE
     * Helper function to find nearest point
     * @param n node to be checked if close to point p
     * @param p point to be checked
     * @param championDistance the current shortest distance
     * @param championPoint the current minimum distance point
     * @param orientation HORIZONTAL or VERTICAL split
     * @return the shortest distance point
     */
    private Point2D nearest(Node n, Point2D p, double championDistance, Point2D championPoint, boolean orientation){

        // if node is null => base case
        if (n == null){
            return championPoint;
        }

        // current point
        Point2D currentPoint = n.p;

        // if the point is exactly the same as the BST point
        if (currentPoint.equals(p)){
            return p;
        }

        // check distance from node to query point
        // using distance squared because sqrt method in normal distance is expensive
        double currentDistance = currentPoint.distanceSquaredTo(p);

        // check if smaller than champion
        if (currentDistance < championDistance){

            // update championPoint & championDistance
            championPoint = currentPoint;
            championDistance = currentDistance;
        }

        // using comparable point to determine direction
        double comp;

        // check which direction to move to
        // check orientations
        // starting from VERTICAL_SPLIT
        if (orientation == VERTICAL_SPLIT){
            // check the given points direction the Node's partition line
            comp = p.x() - currentPoint.x();

            // otherwise HORIZONTAL_SPLIT
        } else {
            comp = p.y() - currentPoint.y();
        }

        // if negative go left of tree
        if (comp < 0){
            championPoint = nearest(n.lb, p, championDistance, championPoint, !orientation);

            // update champion distance with new recursive champion point
            championDistance = championPoint.distanceSquaredTo(p);

            // if new champion ended up being further away, check the other side of tree
            if (championPoint.distanceSquaredTo(p) >= comp * comp) {
                championPoint = nearest(n.rt, p, championDistance, championPoint, !orientation);
            }

        // if positive OR on partition, go right of tree
        } else {
            championPoint = nearest(n.rt, p, championDistance, championPoint, !orientation);

            // update champion distance with new recursive champion point
            championDistance = championPoint.distanceSquaredTo(p);

            // if new champion ended up being further away, check the other side of tree
            if (championPoint.distanceSquaredTo(p) >= comp * comp) {
                championPoint = nearest(n.lb, p, championDistance, championPoint, !orientation);
            }
        }

        return championPoint;
    }



    public static void main(String[] args) {
        // unit testing of the methods (optional)
        // unit testing of the methods (optional)
        Point2D a = new Point2D(0.2, 0.3);
        Point2D b = new Point2D(0.1, 0.13);
        Point2D c = new Point2D(0.4, 0.2);
        Point2D d = new Point2D(0.6, 0.1);
        Point2D e = new Point2D(0.20, 0.8);
        Point2D f = new Point2D(0.5, 0.7);

        KdTree lit = new KdTree();
        lit.insert(a);
        lit.insert(b);
        lit.insert(c);
        lit.insert(d);
        lit.insert(e);
        lit.insert(f);

        Point2D test = new Point2D(0.7,0.2);

        Point2D ans = lit.nearest(test);

        System.out.println(ans);
    }
}