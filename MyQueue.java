import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyQueue<E> implements ALDAQueue<E> {

    private static class Node<E>{

        Node<E> next;
        E data;

        Node(E data){
            this.data = data;
        }

    }

    private Node<E> first = null;
    private Node<E> last = null;

    // Temporary nodes that resets on every call to discriminate()
    private Node<E> discriminatedFirst;
    private Node<E> discriminatedLast;

    private int capacity;
    private int size = 0;

    public MyQueue(int capacity){
        if(capacity <= 0)
            throw new IllegalArgumentException();
        this.capacity = capacity;
    }

    @Override
    public void add(E element) {
        if(element == null) {
            throw new NullPointerException();
        } else if(isFull()){
            throw new IllegalStateException();
        }

        Node<E> newNode = new Node<>(element);

        if(isEmpty())
            last = first = newNode;
         else
            last = last.next = newNode;

        size++;
    }

    @Override
    public void addAll(Collection<? extends E> c) {
        if(c == null)
            throw new NullPointerException();
        else if (c.size() > capacity)
            throw new IllegalStateException();

        for(E element: c)
            if(!isFull())
                add(element);
    }

    @Override
    public E remove() {
        if(isEmpty())
            throw new NoSuchElementException();

        Node<E> nodeToDequeue = first;
        first = first.next;
        size--;

        return nodeToDequeue.data;
    }

    @Override
    public E peek() {
        return isEmpty() ? null: first.data;
    }

    @Override
    public void clear() {
        first = last = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return first == null;
    }

    @Override
    public boolean isFull() {
        return size == capacity;
    }

    @Override
    public int totalCapacity() {
        return capacity;
    }

    @Override
    public int currentCapacity() {
        return capacity - size;
    }

    @Override
    public int discriminate(E element) {
        int count = 0;

        discriminatedFirst = null;
        discriminatedLast = null;

        if(element == null)
            throw new NullPointerException();
        else if(isEmpty())
            return count;

        Node<E> current = first;

        while(current != null) {

            if (current.data.equals(element)){

                addToDiscriminated(element);
                current = first = first.next;
                count++;
                size--;

            } else if(current.next != null && current.next.data.equals(element)) {

                addToDiscriminated(element);
                current.next = current.next.next;
                if(current.next == null)
                    last = current;
                count++;
                size--;

            } else {
                //No match, continue the loop
                current = current.next;
            }
        }

        if(discriminatedFirst != null)
            appendDiscriminated();

        size += count;

        return count;
    }

    private void addToDiscriminated(E element){
        Node<E> nodeToDiscriminate = new Node<>(element);
        if(discriminatedFirst == null)
            discriminatedLast = discriminatedFirst = nodeToDiscriminate;
        else
            discriminatedLast = discriminatedLast.next = nodeToDiscriminate;
    }

    private void appendDiscriminated(){
        if(isEmpty()){
            first = discriminatedFirst;
            last = discriminatedLast;
        } else {
            last.next = discriminatedFirst;
            last = discriminatedLast;
        }
    }

    @Override
    public String toString() {
        boolean isFirstItem = true;
        StringBuilder items = new StringBuilder();
        Iterator<E> itr = iterator();
        while(itr.hasNext()) {
            items.append(isFirstItem ? itr.next() : ", " + itr.next());
            isFirstItem = false;
        }
        return String.format("[%s]", items.toString());
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Node<E> nextNode = first;
            @Override
            public boolean hasNext() {
                return nextNode != null;
            }

            @Override
            public E next() {
                if(!hasNext())
                    throw new NoSuchElementException();
                E data = nextNode.data;
                nextNode = nextNode.next;
                return data;
            }
        };
    }
}

