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
 * Klasa LibraryStopWritingTest testuje metodę stopWriting() w klasie Library,
 * symulując scenariusz, w którym pisarz kończy pisanie.
 */
@RunWith(PowerMockRunner.class)
class LibraryStopWritingTest extends MultithreadedTest {
    private Library library;
    private Semaphore wrtMock;
    private Writer writer;

    /**
     * Metoda inicjalizująca środowisko testowe.
     * Tworzy mocki dla semaforów, inicjalizuje bibliotekę oraz przykładowego pisarza.
     */
    @Override
    public void initialize() {
        wrtMock = PowerMockito.mock(Semaphore.class);
        Semaphore mutexMock = PowerMockito.mock(Semaphore.class);
        writer = new Writer(1, library);

        library = new Library(wrtMock, mutexMock);
    }

    /**
     * Symuluje scenariusz, w którym pisarz rozpoczyna i kończy pisanie w wątku.
     *
     * @throws InterruptedException jeśli operacja jest przerwana.
     */
    void thread1() throws InterruptedException {
        library.startWriting(writer);
        library.stopWriting(writer);
    }

    /**
     * Testuje poprawność działania metody stopWriting().
     * Sprawdza, czy lista pisarzy w bibliotece jest poprawnie aktualizowana
     * oraz czy semafor wrt został prawidłowo zwolniony.
     *
     * @throws Throwable jeśli wystąpi błąd podczas testu.
     */
    @Test
    void testStopWriting() throws Throwable {
        initialize();
        thread1();

        Assertions.assertEquals(0, library.getWritersInLibrary().size());

        verify(wrtMock, times(1)).release();
    }
}