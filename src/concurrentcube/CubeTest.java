package concurrentcube;

import concurrentcube.testFiles.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Timeout;


class CubeTest {
    private final ConcurrentTests concurrentTestClass =
                                    new ConcurrentTests(5);

    @Test
    @Timeout(1)
    public void sequentialTest() throws InterruptedException {
        SequentialTestData.initializeTab();
        for (int i = 1; i <= 5; i++) {
            Cube c = new Cube(i, (a, b) -> {}, (a, b) -> {}, () -> {}, () -> {});
            for (int rotation = 0; rotation < 100; rotation++) {
                int halfLayers = i / 2;
                for (int l = 0; l <= halfLayers; l++) {
                    for (int s = 0; s < 6; s++) {
                        c.rotate(s, l);
                        Assertions.assertEquals(SequentialTestData.next(), c.show());
                    }
                }
            }
        }
    }




    @Test
    @Timeout(2)
    public void concurrentTheSameLayersTest() throws InterruptedException {
        concurrentTestClass.runTheSameLayersTest(0);
        concurrentTestClass.runTheSameLayersTest(1);
        concurrentTestClass.runTheSameLayersTest(2);
    }

    //
    @Test
    @Timeout(1)
    void concurrentThreeAxisTest() {
        concurrentTestClass.runThreeAxisTest();
    }


    // Wiele wątków wykonujących się jednocześnie w jednej osi
    @Test
    @Timeout(5)
    void concurrentManyThreadsOnOneAxisTest() {
        concurrentTestClass.runManyThreadsOnOneAxis(0);
        concurrentTestClass.runManyThreadsOnOneAxis(1);
        concurrentTestClass.runManyThreadsOnOneAxis(2);
    }


}