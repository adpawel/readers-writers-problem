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
 * Klasa LibraryStopReadingTest testuje metodę stopReading() w klasie Library,
 * symulując scenariusz, w którym czytelnik kończy czytanie.
 * Wykorzystuje PowerMockito do mockowania semaforów oraz MultithreadedTest
 * do obsługi środowiska wielowątkowego.
 */
@RunWith(PowerMockRunner.class)
class LibraryStopReadingTest extends MultithreadedTest {
    private Library library;
    private Semaphore wrtMock;
    private Semaphore mutexMock;
    private Reader reader1;

    /**
     * Metoda inicjalizująca środowisko testowe.
     * Tworzy mocki dla semaforów wrt i mutex, inicjalizuje bibliotekę oraz
     * przykładowego czytelnika.
     */
    @Override
    public void initialize() {
        wrtMock = PowerMockito.mock(Semaphore.class);
        mutexMock = PowerMockito.mock(Semaphore.class);
        reader1 = new Reader(1, library);

        library = new Library(wrtMock, mutexMock);
    }

    /**
     * Symuluje scenariusz, w którym czytelnik rozpoczyna i kończy czytanie w wątku.
     *
     * @throws InterruptedException jeśli operacja jest przerwana.
     */
    void thread1() throws InterruptedException {
        library.startReading(reader1);
        library.stopReading(reader1);
    }

    /**
     * Testuje poprawność działania metody stopReading() w warunkach współbieżności.
     * Sprawdza, czy liczba czytelników w bibliotece jest aktualizowana poprawnie,
     * oraz czy interakcje z semaforami są zgodne z oczekiwaniami.
     *
     * @throws Throwable jeśli wystąpi błąd podczas testu.
     */
    @Test
    void testStopReading() throws Throwable {
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
}