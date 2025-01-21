package pl.edu.agh.kis.pz1;

import lombok.Getter;


/**
 * Klasa App zarządza symulacją czytelników i pisarzy korzystających z biblioteki.
 * <p>
 * Program umożliwia uruchomienie wielu wątków reprezentujących czytelników
 * i pisarzy, którzy współdzielą zasoby biblioteki. Domyślna liczba czytelników
 * to 10, a pisarzy 3, ale można je dostosować za pomocą argumentów wejściowych.
 * </p>
 */
@Getter
public class App {
    private static int noReaders = 10;
    private static int noWriters = 3;

    /**
     * Główna metoda aplikacji zarządzającej symulacją czytelników i pisarzy.
     * <p>
     * Metoda tworzy instancję biblioteki oraz w zależności od przekazanych argumentów
     * (liczby czytelników i pisarzy) uruchamia odpowiednią liczbę wątków reprezentujących
     * czytelników i pisarzy. W przypadku braku argumentów domyślne wartości to:
     * 10 czytelników i 3 pisarzy.
     * </p>
     * @param args argumenty wejściowe programu. Pierwszy argument to liczba czytelników,
     *             drugi argument to liczba pisarzy. Jeśli brak argumentów, używane są
     *             wartości domyślne.
     */
    public static void main(String[] args) {
        Library library = new Library();

        if (args.length == 2) {
            try {
                noReaders = Integer.parseInt(args[0]);
                noWriters = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Podane argumenty muszą być liczbami całkowitymi.");
                System.exit(1);
            }
        }

        int smaller = Math.min(noReaders, noWriters);

        for (int i = 0; i < smaller; i++) {
            new Writer(i + 1, library).start();
            new Reader(i + 1, library).start();
        }
        for (int i = smaller; i < noReaders; i++) {
            new Reader(i + 1, library).start();
        }
        for (int i = smaller; i < noWriters; i++) {
            new Writer(i + 1, library).start();
        }
    }
}