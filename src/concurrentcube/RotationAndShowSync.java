package concurrentcube;

import java.util.concurrent.Semaphore;

public class RotationAndShowSync {
    private final Semaphore mutex = new Semaphore(1, true);
    private final Semaphore[] groups = new Semaphore[4];
    private final Semaphore[] layerOccupied;
    private final int[] howManyWaiting = {0, 0, 0, 0};
    private final int[] howManyWorking = {0, 0, 0, 0};

    public RotationAndShowSync(int size) {
        for (int i = 0; i < 4; i++) {
            groups[i] = new Semaphore(0, true);
        }
        layerOccupied = new Semaphore[size];
        for (int i = 0; i < size; i++) {
            layerOccupied[i] = new Semaphore(1, true);
        }
    }

    private int sumArrayWithoutGroupId(int groupId, boolean howManyWaitingArr) {
        int sum = 0;
        for (int i = 0; i < 4; i++) {
            if (i != groupId)
                if (howManyWaitingArr)
                    sum += howManyWaiting[i];
                else
                    sum += howManyWorking[i];
        }
        return sum;
    }

    public void entryProtocol(int groupId, int layer) throws InterruptedException{
        mutex.acquire();
        if (sumArrayWithoutGroupId(groupId, true) +
            sumArrayWithoutGroupId(groupId, false) > 0) {
            howManyWaiting[groupId]++;
            mutex.release();
            groups[groupId].acquireUninterruptibly();
            howManyWaiting[groupId] -= 1;
        }

        howManyWorking[groupId]++;


        if(howManyWaiting[groupId] > 0)
            groups[groupId].release();
        else
            mutex.release();

        if (groupId != 0)
            layerOccupied[layer].acquireUninterruptibly();

        if (Thread.currentThread().isInterrupted()) {
            exitProtocol(groupId, layer);
            throw new InterruptedException();
        }

    }


    public void exitProtocol(int groupId, int layer) {
        if (groupId != 0)
            layerOccupied[layer].release();

        mutex.acquireUninterruptibly();
        howManyWorking[groupId]--;

        if (howManyWorking[groupId] == 0) {
            boolean somebodyWoken = false;
            for (int i = 1; i < 4 && !somebodyWoken; i++) {
                int groupToWake = (groupId + i) % 4;
                if (howManyWaiting[groupToWake] > 0) {
                    somebodyWoken = true;
                    groups[groupToWake].release();
                }
            }
            if (!somebodyWoken)
                mutex.release();
        }
        else {
            mutex.release();
        }
    }

}
