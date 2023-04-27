package concurrentcube;

import java.util.function.BiConsumer;


public class Cube {
    private int size;
    private Runnable beforeShowing, afterShowing;
    private BiConsumer<Integer, Integer> beforeRotation, afterRotation;
    private Wall[] walls = new Wall[6];
    private RotationAndShowSync syncMechanism;


    public Cube(int size, BiConsumer<Integer, Integer> beforeRotation,
                BiConsumer<Integer, Integer> afterRotation,
                Runnable beforeShowing, Runnable afterShowing) {
        this.size = size;
        this.beforeShowing = beforeShowing;
        this.afterShowing = afterShowing;
        this.beforeRotation = beforeRotation;
        this.afterRotation = afterRotation;
        for (int i = 0; i < 6; i++) {
            walls[i] = new Wall(size, i);
        }
        this.syncMechanism = new RotationAndShowSync(size);
    }

    private void rotateWall(int wall, boolean by90) {
        if(by90)
            walls[wall].rotate90();
        else
            walls[wall].rotateMinus90();
    }

    private void rotateUD(int side, int layer) throws InterruptedException {
        int idFromEnd = size - 1 - layer;

        int syncLayer = side == 0 ? layer : idFromEnd;
        syncMechanism.entryProtocol(1, syncLayer);
        beforeRotation.accept(side, layer);

        rotateWallIfNecessary(side, layer);
        if (side == 0) {
            int[] temp = walls[1].getRowArray(layer);
            walls[1].setRowArray(walls[2].getRowArray(layer), layer, false);
            walls[2].setRowArray(walls[3].getRowArray(layer), layer, false);
            walls[3].setRowArray(walls[4].getRowArray(layer), layer, false);
            walls[4].setRowArray(temp, layer, false);
        }
        else if (side == 5) {
            int[] temp = walls[4].getRowArray(idFromEnd);
            walls[4].setRowArray(walls[3].getRowArray(idFromEnd), idFromEnd, false);
            walls[3].setRowArray(walls[2].getRowArray(idFromEnd), idFromEnd, false);
            walls[2].setRowArray(walls[1].getRowArray(idFromEnd), idFromEnd, false);
            walls[1].setRowArray(temp, idFromEnd, false);
        }

        afterRotation.accept(side, layer);
        syncMechanism.exitProtocol(1, syncLayer);
    }

    private void rotateLR(int side, int layer) throws InterruptedException {
        int idFromEnd = size - 1 - layer;

        int syncLayer = side == 3 ? layer : idFromEnd;
        syncMechanism.entryProtocol(2, syncLayer);
        beforeRotation.accept(side, layer);

        rotateWallIfNecessary(side, layer);
        if (side == 3) {
            int[] temp = walls[4].getColumnArray(layer);
            walls[4].setColumnArray(walls[0].getColumnArray(idFromEnd), layer, true);
            walls[0].setColumnArray(walls[2].getColumnArray(idFromEnd), idFromEnd, false);
            walls[2].setColumnArray(walls[5].getColumnArray(idFromEnd), idFromEnd, false);
            walls[5].setColumnArray(temp, idFromEnd, true);
        }
        else if (side == 1) {
            int[] temp = walls[4].getColumnArray(idFromEnd);
            walls[4].setColumnArray(walls[5].getColumnArray(layer), idFromEnd, true);
            walls[5].setColumnArray(walls[2].getColumnArray(layer), layer, false);
            walls[2].setColumnArray(walls[0].getColumnArray(layer), layer, false);
            walls[0].setColumnArray(temp, layer, true);
        }

        afterRotation.accept(side, layer);
        syncMechanism.exitProtocol(2, syncLayer);
    }

    private void rotateFB(int side, int layer) throws InterruptedException {
        int idFromEnd = size - 1 - layer;

        int syncLayer = side == 2 ? layer : idFromEnd;
        syncMechanism.entryProtocol(3, syncLayer);
        beforeRotation.accept(side, layer);

        rotateWallIfNecessary(side, layer);
        if (side == 2) {
            int[] temp = walls[1].getColumnArray(idFromEnd);
            walls[1].setColumnArray(walls[5].getRowArray(layer), idFromEnd, false);
            walls[5].setRowArray(walls[3].getColumnArray(layer), layer, true);
            walls[3].setColumnArray(walls[0].getRowArray(idFromEnd), layer, false);
            walls[0].setRowArray(temp, idFromEnd, true);
        }
        else if (side == 4) {
            int[] temp = walls[3].getColumnArray(idFromEnd);
            walls[3].setColumnArray(walls[5].getRowArray(idFromEnd), idFromEnd,true);
            walls[5].setRowArray(walls[1].getColumnArray(layer), idFromEnd, false);
            walls[1].setColumnArray(walls[0].getRowArray(layer), layer, true);
            walls[0].setRowArray(temp, layer, false);
        }

        afterRotation.accept(side, layer);
        syncMechanism.exitProtocol(3, syncLayer);
    }

    private int oppositeWall(int i) {
        int[] opositeWalls = {5, 3, 4, 1, 2, 0};
        return opositeWalls[i];
    }


    private void rotateWallIfNecessary(int side, int layer) {
        if (layer == 0)
            rotateWall(side, true);
        else if (layer == size - 1)
            rotateWall(oppositeWall(side), false);
    }

    public void rotate(int side, int layer) throws InterruptedException {
        if (side == 0 || side ==5)
            rotateUD(side, layer);
        else if (side == 1 || side == 3)
            rotateLR(side, layer);
        else
            rotateFB(side, layer);
    }

    public void printCube() {
        String blankSpace = " ".repeat(size + 1);
        for (int i= 0; i < size; i++) {
            System.out.println(blankSpace + walls[0].rowString(i));
        }

        for (int i = 0; i < size; i++) {
            System.out.println(walls[1].rowString(i) + walls[2].rowString(i) +
                                walls[3].rowString(i) + walls[4].rowString(i));
        }

        for (int i= 0; i < size; i++) {
            System.out.println(blankSpace + walls[5].rowString(i));
        }

        System.out.println();
    }

    public String show() throws InterruptedException {
        syncMechanism.entryProtocol(0, -1);
        beforeShowing.run();

        StringBuilder cubeState = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            cubeState.append(walls[i].toString());
        }

        afterShowing.run();
        syncMechanism.exitProtocol(0, -1);

        return cubeState.toString();
    }
}
