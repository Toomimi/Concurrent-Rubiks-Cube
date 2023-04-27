package concurrentcube;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {


    private static void generateTest() {

        try {
            FileWriter myWriter = new FileWriter("valid.out");
            int counter = 0;
            for (int i = 1; i <= 5; i++) {
                Cube c = new Cube(i, (a, b) -> {}, (a, b) -> {}, () -> {}, () -> {});
                for (int rotation = 0; rotation < 100; rotation++) {
                    int halfLayers = i / 2;
                    for (int l = 0; l <= halfLayers; l++) {
                        for (int s = 0; s < 6; s++) {
                        c.rotate(s, l);
                        myWriter.write("tab[" + counter + "] = \"" + c.show() + "\";" + '\n');
                        counter++;
                        }
                    }
                }
            }

            myWriter.close();
        }
        catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        // Example of usage
        Cube c = new Cube(4, (a, b)->{}, (a, b)->{}, ()->{}, ()->{});

        System.out.println("Created cube of size 4");
        c.printCube();
        System.out.println("Rotating layer number 1 Down side");
        c.rotate(5, 0);
        System.out.println("Rotating layer number 0 Up side");
        c.rotate(0, 0);
        System.out.println("New state:");
        c.printCube();


        generateTest();


    }
}
