import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.util.Arrays;


public class SeamCarver {
    private int[][] pixelArray;
    private double[][] energyArray;
    private int currentWidth;
    private int currentHeight;
    private double[][] distTo;          // distTo[v] = distance of shortest s -> v path
    private int[][] edgeTo;             // edgeTo[v] = last edge on shortest s -> v path
    private double seamDist;
    private int seamSink;
    private boolean isTransposed;

    /**
     * create a seam carver object based on the given picture
     * @param picture object to be carved
     */
    public SeamCarver(Picture picture) {

        // handling corner cases
        if (picture == null) {
            throw new IllegalArgumentException("Cannot accept null");
        }

        // initializing adjusted dimensions
        currentWidth = picture.width();
        currentHeight = picture.height();

        // initializing an RGB pixel array
        pixelArray = new int[width()][height()];
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                pixelArray[col][row] = picture.get(col,row).getRGB();
            }
        }

        // initializing an energy array
        energyArray = new double[width()][height()];
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                energyArray[col][row] = calculateEnergy(col, row);
            }
        }

        // setting is transposed
        isTransposed = false;
    }

    /**
     * @return current picture
     */
    public Picture picture() {

        // initialize a new picture object
        Picture picture = new Picture(width(),height());

        // set colors based on the color array
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {

                // create a color object
                Color setColorPixel = new Color(pixelArray[col][row]);

                // overwrite empty pixel with an actual colour
                picture.set(col, row, setColorPixel);
            }
        }

        // return current created picture
        return picture;
    }

    /**
     * @return width of current picture
     */
    public int width() {
      return currentWidth;
    }

    /**
     * @return height of current picture
     */
    public int height() {
        return currentHeight;
    }

    /**
     * @param x column of pixel
     * @param y row of pixel
     * @return energy of pixel at column x and row y via array
     */
    public double energy(int x, int y) {

        // handling corner cases
        if ((x < 0 || x >= width()) || (y < 0 || y >= height())) {
            throw new IllegalArgumentException("X or Y is beyond index bounds");
        }

        return energyArray[x][y];
    }

    /**
     * @return sequence of indices for horizontal seam
     */
    public int[] findHorizontalSeam() {

        transpose();
        int[] seam = findSeam();
        transpose();

        return seam;
    }

    /**
     * @return sequence of indices for vertical seam
     */
    public int[] findVerticalSeam() {

        return findSeam();
    }

    /**
     * remove horizontal seam from current picture
     * basically same as vertical removal except pixel transferring is different
     * @param seam to be removed (horizontal)
     */
    public void removeHorizontalSeam(int[] seam) {

        transpose();
        removeVerticalSeam(seam);
        transpose();

    }

    /**
     * remove vertical seam from current picture
     * @param seam to be removed (vertical)
     */
    public void removeVerticalSeam(int[] seam) {

        // handling corner cases
        if (seam == null) {
            throw new IllegalArgumentException("Cannot accept null");
        }
        if (width() <= 1) {
            throw new IllegalArgumentException("Picture width is less than or equal to 1");
        }
        if (seam.length != height()) {
            throw new IllegalArgumentException("Seam length is invalid");
        }

        // checking the seam elements one by one
        int checkLast = seam[0];
        for (int check: seam) {
            if (check >= width() || check < 0) {
                throw new IllegalArgumentException("Index reference is out of bounds");
            }
            else if (Math.abs(check - checkLast) > 1) {
                throw new IllegalArgumentException("Index is not adjacent");
            }
            checkLast = check;
        }

        // initialize new arrays
        int[][] newPixelArray = new int[width() - 1][height()];
        double[][] newEnergyArray = new double[width() - 1][height()];

        // loop row by row with new seam index
        for (int row = 0; row < height(); row++) {
            int split = seam[row];

            // copy left half before seam
            for (int col = 0; col < split; col++) {
                newPixelArray[col][row] = pixelArray[col][row];
                newEnergyArray[col][row] = energyArray[col][row];
            }

            // copy right half after seam
            for (int col = split + 1; col < width(); col++) {
                newPixelArray[col - 1][row] = pixelArray[col][row];
                newEnergyArray[col - 1][row] = energyArray[col][row];
            }
        }

        // adjust instance variables
        pixelArray = newPixelArray;
        energyArray = newEnergyArray;
        currentWidth--;

        // adjust energy values along the seam
        for (int row = 0; row < height(); row++) {
            int split = seam[row];

            // PIXEL LOCATIONS
            // right end pixel removed
            // adjust the column before the seams
            if (split == width()) {
                energyArray[split - 1][row] = calculateEnergy(split - 1, row);
            }

            // left end pixel removed
            // adjust the column which got shifted into the former seam position
            else if (split == 0) {
                energyArray[split][row] = calculateEnergy(split, row);
            }

            // middle pixel removed
            // adjust the column before the seams
            // adjust the column which got shifted into the former seam position
            else {
                energyArray[split - 1][row] = calculateEnergy(split - 1, row);
                energyArray[split][row] = calculateEnergy(split, row);
            }
        }
    }

    /***************************************************************
     *                      HELPER FUNCTIONS
     ***************************************************************/

    /**
     *
     * @param col the column location of the pixel in the array
     * @param row the row location of the pixel in the array
     * @return return the energy converted from RGB pixel
     */
    private double calculateEnergy(int col, int row) {

        // handling corner cases
        if ((col < 0 || col >= width()) || (row < 0 || row >= height())) {
            throw new IllegalArgumentException("X or Y is beyond index bounds");
        }
        if ((col == 0 || col == width() - 1) || (row == 0 || row == height() - 1)) {
            // if border pixels
            return 1000;
        }

        // for pixel[col][row], storing variables to calculate dual-gradient energy function
        int top = pixelArray[col][row - 1];
        int bottom = pixelArray[col][row + 1];
        int left = pixelArray[col - 1][row];
        int right = pixelArray[col + 1][row];

        // using dual-gradient energy formula
        double dx2 = Math.pow(getRGB(right, 'r') - getRGB(left, 'r'), 2) +
                Math.pow(getRGB(right, 'g') - getRGB(left, 'g'), 2) +
                Math.pow(getRGB(right, 'b') - getRGB(left, 'b'), 2);

        double dy2 = Math.pow(getRGB(bottom, 'r') - getRGB(top, 'r'), 2) +
                Math.pow(getRGB(bottom, 'g') - getRGB(top, 'g'), 2) +
                Math.pow(getRGB(bottom, 'b') - getRGB(top, 'b'), 2);

        return Math.sqrt(dx2 + dy2);
    }

    private int getRGB(int rgb, char colour) {

        // grab either red, green or blue from pixel
        switch (colour) {
            case 'r':
                return (rgb >> 16) & 0xFF;

            case 'g':
                return (rgb >> 8) & 0xFF;

            case 'b':
                return rgb & 0xFF;
        }

        // else return error
        return -1;
    }

    /**
     * transposes energyArray and pixelArray for horizontal manipulation
     */
    private void transpose () {

        // swap dimensions
        int temp = currentWidth;
        currentWidth = currentHeight;
        currentHeight = temp;

        // create empty transpose arrays for pixels and energy
        double[][] transposeEnergy = new double[width()][height()];
        int[][] transposePixel = new int[width()][height()];

        // loop over original array
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                transposeEnergy[col][row] = energyArray[row][col];
                transposePixel[col][row] = pixelArray[row][col];
            }
        }

        // reinitialize pixel and energy arrays
        pixelArray = transposePixel;
        energyArray = transposeEnergy;
        isTransposed = !isTransposed;
    }

    /**
     * relaxes vertices according to pixel position
     * @param col
     * @param row
     */
    private void traverse(int col, int row){

        // Bottom (Sink) End Pixel: relax just the sink vertex
        if (row == height() - 1){
            relax(col, row);
        }

        // Right End Pixel: relax below and to the left
        else if (col == width() - 1) {
            relax(col, row, col - 1, row + 1);
            relax(col, row, col, row + 1);
        }

        // Left End Pixel: relax below and to the right
        else if (col == 0) {
            relax(col, row, col, row + 1);
            relax(col, row, col + 1, row + 1);
        }

        // Body Pixel: relax left, below and right
        else {
            relax(col, row, col - 1, row + 1);
            relax(col, row, col, row + 1);
            relax(col, row, col + 1, row + 1);
        }

    }

    /**
     * Relax the Seam Sink Pixel
     * @param col column of Sink Pixel
     * @param row row of Sink Pixel
     */
    private void relax(int col, int row) {

        // check if a shorter distance was found
        if (seamDist > distTo[col][row]) {

                // save new distance and edge
                seamDist = distTo[col][row];
                seamSink = col;
            }
    }

    /**
     * Relax Pixel 2 when comparing from Pixel 1
     * @param col1 column of Pixel 1
     * @param row1 row of Pixel 1
     * @param col2 column of Pixel 2
     * @param row2 row of Pixel 2
     */
    private void relax(int col1, int row1, int col2, int row2) {

        // check if a shorter distance was found
        if (distTo[col2][row2] > (distTo[col1][row1] + energyArray[col2][row2])) {

            // save new distance and edge
            distTo[col2][row2] = distTo[col1][row1] + energyArray[col2][row2];
            edgeTo[col2][row2] = col1;
        }
    }



    /**
     * must be vertically positioned
     * just like percolation assume source vertex is implicit sitting above image
     * all of the top-row pixels are adjacent to this source vertex
     * assume the sink vertex as an explicit vertex sitting below image
     * adjacent to all bottom-row pixels
     * @return sequence of indices for vertical seam
     */
    private int[] findSeam(){

        // reinitialize shortest path search values
        seamSink = Integer.MAX_VALUE;
        seamDist = Double.POSITIVE_INFINITY;
        distTo = new double[width()][height()];
        edgeTo = new int[width()][height()];

        // fill up arrays with default values
        for (double[] toFill : distTo) {
            Arrays.fill(toFill, Double.POSITIVE_INFINITY);
        }
        for (int[] toFill : edgeTo) {
            Arrays.fill(toFill, Integer.MAX_VALUE);
        }

        // relax the top row as default values for top border pixels
        for (int col = 0; col < width(); col++) {
            distTo[col][0] = 1000;
            edgeTo[col][0] = -1;
        }

        // reverse DFS post-order => topological order
        // moving diagonally to the right starting from the top right corner the push left
        for (int top = width() - 1; top >= 0; top--) {
            for (int depth = 0; depth < height() && depth + top < width(); depth++){
                traverse(depth + top, depth);
            }
        }

        // start at the second top row
        // visit pixels from the left side then move diagonally to the right
        // capture remaining pixels
        for (int depth = 1; depth < height(); depth++) {
            for (int moveRight = 0; moveRight < width() && (depth + moveRight) < height(); moveRight++) {
                traverse(moveRight, depth + moveRight);
            }
        }

        // create a seam with the shortest path
        int[] seam = new int[height()];

        // set last index as the sink variable
        seam[height() - 1] = seamSink;

        // fill up seam line
        for (int row = height() - 1; row > 0; row--) {
            seam[row - 1] = edgeTo[seam[row]][row];
        }

        // clean up seam variables
        distTo = null;
        edgeTo = null;

        return seam;
    }

    // for unit testing
    public static void main(String[] args) {

        Picture picture = new Picture(args[0]);
        StdOut.printf("image is %d columns by %d rows\n", picture.width(), picture.height());
        SeamCarver sc = new SeamCarver(picture);

        for (int i = 0; i < sc.width(); i++){
            for (int j = 0; j < sc.height(); j++) {
                System.out.println(sc.energyArray[i][j]);
            }
        }

        System.out.println(sc.height());
    }
}
