package t5heaps;

// Klassen i denna fil måste döpas om till DHeap för att testerna ska fungera.

//DHeap class
//
//CONSTRUCTION: with optional capacity (that defaults to 100)
//            or an array containing initial items
//
//******************PUBLIC OPERATIONS*********************
//void insert( x )       --> Insert x
//Comparable deleteMin( )--> Return and remove smallest item
//Comparable findMin( )  --> Return smallest item
//boolean isEmpty( )     --> Return true if empty; else false
//void makeEmpty( )      --> Remove all items
//******************ERRORS********************************
//Throws UnderflowException as appropriate

/**
 * Implements a binary heap.
 * Note that all "matching" is based on the compareTo method.
 *
 * @author Mark Allen Weiss
 */
public class DHeap<AnyType extends Comparable<? super AnyType>> {

    private static final int DEFAULT_CAPACITY = 10;

    private int currentSize;      // Number of elements in heap
    private AnyType[] array;      // The heap array
    private int dimension;

    /**
     * Constructs a binary heap.
     */
    public DHeap() {
        this(2);
    }

    public DHeap(int dimension) {
        this(DEFAULT_CAPACITY, dimension);
    }

    /**
     * Construct the heap.
     *
     * @param capacity  the capacity of the heap.
     * @param dimension the dimension of the heap. (Amount of children for each node)
     */
    public DHeap(int capacity, int dimension) {
        if (dimension < 2)
            throw new IllegalArgumentException();
        currentSize = 0;
        this.dimension = dimension;
        array = (AnyType[]) new Comparable[capacity + 1];
    }

    /**
     * Insert into the priority queue, maintaining heap order.
     * Duplicates are allowed.
     *
     * @param x the item to insert.
     */
    public void insert(AnyType x) {
        if(x == null)
            throw new IllegalArgumentException();

        if (currentSize == array.length - 1)
            enlargeArray(array.length * 2 + 1);

        // Percolate up
        int hole = ++currentSize;
        for (array[0] = x; hole > 1 && x.compareTo(array[parentIndex(hole)]) < 0; hole = parentIndex(hole))
            array[hole] = array[parentIndex(hole)];
        array[hole] = x;
    }


    private void enlargeArray(int newSize) {
        AnyType[] old = array;
        array = (AnyType[]) new Comparable[newSize];
        for (int i = 0; i < old.length; i++)
            array[i] = old[i];
    }

    /**
     * Find the smallest item in the priority queue.
     *
     * @return the smallest item, or throw an UnderflowException if empty.
     */
    public AnyType findMin() {
        if (isEmpty())
            throw new UnderflowException();
        return array[1];
    }

    /**
     * Remove the smallest item from the priority queue.
     *
     * @return the smallest item, or throw an UnderflowException if empty.
     */
    public AnyType deleteMin() {
        if (isEmpty())
            throw new UnderflowException();

        AnyType minItem = findMin();
        array[1] = array[currentSize--];
        percolateDown(1);

        return minItem;
    }

    /**
     * Establish heap order property from an arbitrary
     * arrangement of items. Runs in linear time.
     */
    private void buildHeap() {
        for (int i = currentSize / 2; i > 0; i--)
            percolateDown(i);
    }

    /**
     * Test if the priority queue is logically empty.
     *
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return currentSize == 0;
    }

    /**
     * Make the priority queue logically empty.
     */
    public void makeEmpty() {
        currentSize = 0;
    }

    public int size() {
        return currentSize;
    }

    public AnyType get(int index) {
        return array[index];
    }

    public int firstChildIndex(int parent) {
        if (parent < 1)
            throw new IllegalArgumentException();

        return parent * dimension - (dimension - 2);
    }


    //The above equation but reversed to find parent instead of child
    public int parentIndex(int child) {
        if (child <= 1)
            throw new IllegalArgumentException();

        return (child + (dimension - 2)) / dimension;
    }



    /**
     * Internal method to percolate down in the heap.
     *
     * @param hole the index at which the percolate begins.
     */
    private void percolateDown(int hole) {
        int child;
        AnyType tmp = array[hole];

        for (; firstChildIndex(hole) <= currentSize; hole = child) {
            child = firstChildIndex(hole);
            int minChild = child;
            /*
             * Loops the same amount of times as amount of children -1 since
             * the first child is already known
             */
            for (int i = 0; i < dimension - 1; i++) {
                child++;
                if (child <= currentSize && array[child].compareTo(array[minChild]) < 0) {
                    minChild = child;
                }
            }

            child = minChild;

            if (array[child].compareTo(tmp) < 0)
                array[hole] = array[child];
            else
                break;
        }
        array[hole] = tmp;
    }

    // Test program
    public static void main(String[] args) {
        int numItems = 10000;
        DHeap<Integer> h = new DHeap<>();
        int i = 37;

        for (i = 37; i != 0; i = (i + 37) % numItems)
            h.insert(i);
        for (i = 1; i < numItems; i++)
            if (h.deleteMin() != i)
                System.out.println("Oops! " + i);

    }
}
