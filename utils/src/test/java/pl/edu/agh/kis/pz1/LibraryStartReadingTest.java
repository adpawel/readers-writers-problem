package pl.edu.agh.kis.pz1;

import edu.umd.cs.mtc.MultithreadedTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.Semaphore;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Klasa LibraryStartReadingTest testuje metodę startReading() w klasie Library
 * w kontekście wielowątkowym. Wykorzystuje PowerMockito do mockowania semaforów
 * i środowisko MultithreadedTest do symulacji współbieżności.
 */
@RunWith(PowerMockRunner.class)
class LibraryStartReadingTest extends MultithreadedTest {
    private Library library;
    private Semaphore wrtMock;
    private Semaphore mutexMock;

    /**
     * Metoda inicjalizująca środowisko testowe.
     * Tworzy mocki dla semaforów i instancję klasy Library.
     */
    @Override
    public void initialize() {
        wrtMock = PowerMockito.mock(Semaphore.class);
        mutexMock = PowerMockito.mock(Semaphore.class);

        library = new Library(wrtMock, mutexMock);
    }

    /**
     * Symuluje operację rozpoczęcia czytania przez pierwszego czytelnika w wątku testowym.
     *
     * @throws InterruptedException jeśli operacja jest przerwana.
     */
    void thread1() throws InterruptedException {
        library.startReading(new Reader(1, library));
    }

    /**
     * Symuluje operację rozpoczęcia czytania przez drugiego czytelnika w wątku testowym.
     *
     * @throws InterruptedException jeśli operacja jest przerwana.
     */
    void thread2() throws InterruptedException {
        library.startReading(new Reader(2, library));
    }

    /**
     * Testuje działanie metody startReading() w warunkach współbieżności.
     * Sprawdza liczbę czytelników w czytelni oraz interakcje z semaforami.
     *
     * @throws Throwable jeśli wystąpi błąd podczas testu.
     */
    @Test
    void testStartReading() throws Throwable {
        initialize();
        thread1();
        thread2();

        Assertions.assertEquals(2, library.getReaderCount());
        Assertions.assertEquals(2, library.getReadersInLibrary().size());

        try {
            verify(wrtMock, times(1)).acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        verify(mutexMock, times(2)).release();
    }
}