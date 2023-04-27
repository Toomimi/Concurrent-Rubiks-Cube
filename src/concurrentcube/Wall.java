package concurrentcube;

import java.util.Arrays;

public class Wall {
    private final int size;
    private final int colour;
    private int[][] values;


    private void initializeValues() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                values[i][j] = colour;
            }
        }
    }

    public Wall(int size, int colour) {
        this.size = size;
        this.colour = colour;
        values = new int[size][size];
        initializeValues();
    }

    public int[] getColumnArray(int columnNumber) {
        int[] column = new int[size];
        for (int i = 0; i < size; i++) {
            column[i] = values[i][columnNumber];
        }
        return column;
    }

    public int[] getRowArray(int rowNumber) {
        int[] row = new int[size];
        for (int j = 0; j < size; j++) {
            row[j] = values[rowNumber][j];
        }
        return row;
    }

    public void setColumnArray(int[] columnArray, int columnNumber,
                                                            boolean reverse) {
        if (reverse)
            for (int i = 0, j = size - 1; i < size ; i++, j--)
                values[i][columnNumber] = columnArray[j];
        else
            for (int i = 0; i < size; i++)
                values[i][columnNumber] = columnArray[i];
    }

    public void setRowArray(int[] rowArray, int rowNumber, boolean reverse) {
        if (reverse)
            for (int i = 0, j = size - 1; i < size ; i++, j--)
                values[rowNumber][i] = rowArray[j];
        else
            for (int i = 0; i < size; i++)
                values[rowNumber][i] = rowArray[i];
    }

    public void rotate90() {
        for (int i = 0; i < size - 1; i++) {
            for (int j = i; j < size - 1 - i; j++) {
                swap(i, j, j, size - 1 - i);
                swap(i, j, size - 1 - i, size - 1 - j);
                swap(i, j, size - 1 - j, i);
            }
        }
    }

    public void rotateMinus90() {
        for(int i = 0; i < size - 1; i++){
            for(int j = i; j < size - 1 - i; j++){
                swap(i, j, j, size - 1 - i);
                swap(j, size - 1 - i, size - 1 - j, i);
                swap(j, size - 1 - i, size - 1 - i, size - 1 - j);
            }
        }
    }

    private void swap(int aI, int aJ, int bI, int bJ) {
        int temp = values[aI][aJ];
        values[aI][aJ] = values[bI][bJ];
        values[bI][bJ] = temp;
    }

    @Override
    public String toString() {
        StringBuilder wallState = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                wallState.append(values[i][j]);
            }
        }
        return wallState.toString();
    }

    public String rowString(int rowNumber) {
        StringBuilder row = new StringBuilder();
        for (int j = 0; j < size; j++) {
            row.append(values[rowNumber][j]);
            if (j == size - 1 && colour != 0 && colour != 5 && colour != 4) {
                row.append('|');
            }
        }
        return row.toString();
    }
}
