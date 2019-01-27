package t1linear;

public class MyQueueTest {
    public static void main(String[] args){
        MyQueue<String> queue = new MyQueue<>(10000008);
        System.out.println(queue);
        long startTime = System.nanoTime();

        for(int i = 0; i < 10000000; i++)
            queue.add("A");
        queue.add("A");
        queue.add("A");
        queue.add("B");
        queue.add("A");
        queue.add("A");
        queue.add("C");
        queue.add("A");
        queue.add("A");
        System.out.println(queue);

        queue.discriminate("A");
        int size = queue.size();
        long elapsedNanos = System.nanoTime() - startTime;

        System.out.println("Time to calculate size: " + size + " " + (double) elapsedNanos / 1000000000 + " sec");

    }
}
