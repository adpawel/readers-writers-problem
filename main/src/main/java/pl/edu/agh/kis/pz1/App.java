package pl.edu.agh.kis.pz1;

/**
 * Main
 */
public class App {
    public static void main(String[] args) {
        Library library = new Library();

        int noReaders = 10;
        int noWriters = 4;


        for (int i = 0; i < noReaders; i++) {
            new Reader(i + 1, library).start();
        }
        for (int i = 0; i < noWriters; i++) {
            new Writer(i + 1, library).start();
        }
    }
}
