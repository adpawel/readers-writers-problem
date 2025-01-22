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
 * Klasa LibraryReadingTest zawiera testy jednostkowe i wielowątkowe dla klasy Library.
 */
@RunWith(PowerMockRunner.class)
class LibraryReadingTest extends MultithreadedTest {
    private Library library;
    private Semaphore wrtMock;
    private Semaphore mutexMock;
    private Reader reader1;

    /**
     * Metoda inicjalizująca środowisko testowe.
     * Tworzy mocki dla semaforów oraz czytelnika.
     */
    @Override
    public void initialize() {
        wrtMock = PowerMockito.mock(Semaphore.class);
        mutexMock = PowerMockito.mock(Semaphore.class);
        reader1 = new Reader(1, library);

        library = new Library(wrtMock, mutexMock);
    }

    /**
     * Symuluje operację czytania przez czytelnika w wątku testowym.
     *
     * @throws InterruptedException jeśli operacja jest przerwana.
     */
    void thread1() throws InterruptedException {
        library.reading(reader1);
    }

    /**
     * Testuje poprawność działania metody reading() w kontekście wielowątkowym.
     * Sprawdza interakcje z semaforami oraz zmiany stanu biblioteki.
     *
     * @throws Throwable jeśli wystąpi błąd podczas testu.
     */
    @Test
    void testReading() throws Throwable {
        initialize();
        thread1();

        Assertions.assertEquals(0, library.getReaderCount());
        Assertions.assertEquals(0, library.getReadersInLibrary().size());

        try {
            verify(wrtMock, times(1)).acquire();
            verify(mutexMock, times(2)).acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        verify(mutexMock, times(2)).release();      // raz w startReading i raz w stopReading
        verify(wrtMock, times(1)).release();
    }

    /**
     * Testuje konstruktory klasy Library, weryfikując liczbę maksymalnie dozwolonych czytelników.
     */
    @Test
    void testConstructor(){
        Library l1 = new Library();
        Library l2 = new Library(7);

        assertEquals(5, l1.getNoAllowedReaders());
        assertEquals(7, l2.getNoAllowedReaders());
    }
}