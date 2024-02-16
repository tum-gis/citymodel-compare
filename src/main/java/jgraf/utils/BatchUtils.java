package jgraf.utils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BatchUtils {
    public static final int BATCH_SIZE = 1000; // TODO Set this in a central config file?

    // Divide a queue into batches while removing each element from the queue

    // Divide a large queue into smaller queues of given size, while removing each element from the large queue
    public static <T> List<Queue<T>> toBatches(Queue<T> queue, int batchSize) {
        List<Queue<T>> batches = Collections.synchronizedList(new LinkedList<>());
        while (!queue.isEmpty()) {
            Queue<T> batch = new ConcurrentLinkedQueue<>();
            for (int i = 0; i < batchSize && !queue.isEmpty(); i++) {
                batch.add(queue.poll());
            }
            batches.add(batch);
        }
        return batches;
    }

    // Divide a list into batches of given size, while removing each element from the list
    public static <T> List<List<T>> toBatches(List<T> list, int batchSize) {
        List<List<T>> batches = Collections.synchronizedList(new LinkedList<>());
        Iterator<T> iter = list.iterator();
        while (iter.hasNext()) {
            List<T> batch = Collections.synchronizedList(new LinkedList<>());
            for (int i = 0; i < batchSize && iter.hasNext(); i++) {
                batch.add(iter.next());
                iter.remove();
            }
            batches.add(batch);
        }
        return batches;
    }

    // Divide a set into batches of given size, while removing each element from the set
    public static <T extends Comparable<?>> List<Set<T>> toBatches(Set<T> set, int batchSize) {
        List<Set<T>> batches = Collections.synchronizedList(new LinkedList<>());
        Iterator<T> iter = set.iterator();
        while (iter.hasNext()) {
            Set<T> batch = new ConcurrentSkipListSet<>();
            for (int i = 0; i < batchSize && iter.hasNext(); i++) {
                batch.add(iter.next());
                iter.remove();
            }
            batches.add(batch);
        }
        return batches;
    }


    // Convert a stream to a stream of batches of given size
    // Source: https://stackoverflow.com/a/59164175/5360833
    public static <T> Stream<List<T>> toBatches(Stream<T> stream, int size) {
        Iterator<T> iterator = stream.iterator();
        Iterator<List<T>> listIterator = new Iterator<>() {

            public boolean hasNext() {
                return iterator.hasNext();
            }

            public List<T> next() {
                List<T> result = new ArrayList<>(size);
                for (int i = 0; i < size && iterator.hasNext(); i++) {
                    result.add(iterator.next());
                }
                return result;
            }
        };
        return StreamSupport.stream(((Iterable<List<T>>) () -> listIterator).spliterator(), false);
    }
}
