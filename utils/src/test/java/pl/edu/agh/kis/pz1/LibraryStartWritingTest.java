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
 * Klasa LibraryStartWritingTest testuje metodę startWriting() w klasie Library
 * w kontekście wielowątkowym. Wykorzystuje PowerMockito do mockowania semaforów
 * i środowisko MultithreadedTest do symulacji współbieżności.
 */
@RunWith(PowerMockRunner.class)
class LibraryStartWritingTest extends MultithreadedTest {
    private Library library;
    private Semaphore wrtMock;

    /**
     * Metoda inicjalizująca środowisko testowe.
     * Tworzy mocki dla semafora wrt oraz instancję klasy Library.
     */
    @Override
    public void initialize() {
        wrtMock = PowerMockito.mock(Semaphore.class);
        Semaphore mutexMock = PowerMockito.mock(Semaphore.class);

        library = new Library(wrtMock, mutexMock);
    }

    /**
     * Symuluje operację rozpoczęcia pisania przez pisarza w wątku testowym.
     *
     * @throws InterruptedException jeśli operacja jest przerwana.
     */
    void thread1() throws InterruptedException {
        library.startWriting(new Writer(1, library));
    }

    /**
     * Testuje działanie metody startWriting() w warunkach współbieżności.
     * Sprawdza, czy liczba pisarzy w bibliotece została poprawnie zaktualizowana
     * oraz czy semafor wrt został zablokowany.
     *
     * @throws Throwable jeśli wystąpi błąd podczas testu.
     */
    @Test
    void testStartWriting() throws Throwable {
        initialize();
        thread1();

        Assertions.assertEquals(1, library.getWritersInLibrary().size());
        try {
            verify(wrtMock, times(1)).acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
