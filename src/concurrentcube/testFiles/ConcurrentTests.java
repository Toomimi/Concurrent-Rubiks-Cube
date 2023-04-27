package concurrentcube.testFiles;

import concurrentcube.Cube;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.concurrent.atomic.AtomicInteger;


public class ConcurrentTests {
    private final int cubeSize;
    private Cube c;
    private Thread[] threads;


    public ConcurrentTests(int cubeSize) {
        this.cubeSize = cubeSize;
    }

    private BiConsumer<Integer, Integer> getSleeper(int sleepingTime) {
        return  (a, b) -> {
            try {
                Thread.sleep(sleepingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    private void initializeCube(BiConsumer<Integer, Integer> bR,
                                BiConsumer<Integer, Integer> aR) {
        c = new Cube(cubeSize, bR, aR, () -> {}, () -> {});
    }

    private Duration runThreads() {
        Instant start = Instant.now();
        for (Thread t : threads) {
            t.start();
        }

        try {
            for (Thread t : threads) {
                t.join();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main interrupted");
        }

        return Duration.between(start, Instant.now());
    }

    private void initializeTheSameLayersTest(int side, int threadNumber) {
        threads = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            threads[i] = new Thread(() -> {
                try {
                    c.rotate(side, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void runTheSameLayersTest(int side) throws InterruptedException {
        initializeCube(getSleeper(100), (a, b) -> {});
        String solved = c.show();

        int threadNumber = 4;
        initializeTheSameLayersTest(side, threadNumber);
        Duration duration = runThreads();

        Assertions.assertTrue(duration.toMillis() >= 100L * threadNumber);
        Assertions.assertEquals(solved, c.show());
    }

    private void initializeThreeAxisTest(int threadNumber) {
        threads = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            int side = i % 6;
            threads[i] = new Thread(() -> {
                try {
                    c.rotate(side, ThreadLocalRandom.current().nextInt(cubeSize));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void runThreeAxisTest() {
        int threadNumber = 6;
        initializeCube(getSleeper(100), (a, b) -> {});
        initializeThreeAxisTest(threadNumber);
        Duration duration = runThreads();

        Assertions.assertTrue(duration.toMillis() < 100L * threadNumber);
        Assertions.assertTrue(duration.toMillis() > 100L * threadNumber / 2);
    }

    public void initializeManyThreadsOnOneAxisTest(int side, int threadNumber,
                                                   int threadRotations) {
        threads = new Thread[threadNumber];
        int oppositeSide = (side == 0) ? 5 : (side == 1) ? 3 : 4;
        for (int i = 0; i < threadNumber; i++) {
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < threadRotations; j++) {
                        boolean rotateOppositeSide =
                                ThreadLocalRandom.current().nextBoolean();
                        c.rotate(rotateOppositeSide ? side : oppositeSide,
                                ThreadLocalRandom.current().nextInt(cubeSize));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    public void runManyThreadsOnOneAxis(int side) {
        int sleepingTime = 5;

        AtomicInteger counter = new AtomicInteger(0);
        BiConsumer<Integer, Integer> beforeRotation = (a, b) -> {
            try {
                Thread.sleep(sleepingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter.incrementAndGet();
        };
        BiConsumer<Integer, Integer> afterRotation = (a, b) -> {counter.incrementAndGet();};

        initializeCube(beforeRotation, afterRotation);
        int threadNumber = 100, threadRotations = 10;
        initializeManyThreadsOnOneAxisTest(side, threadNumber, threadRotations);
        Duration duration = runThreads();
        Assertions.assertTrue(duration.toMillis() < threadNumber * threadRotations * sleepingTime);
        Assertions.assertEquals(counter.get(), threadNumber * 2 * threadRotations);
    }
}




