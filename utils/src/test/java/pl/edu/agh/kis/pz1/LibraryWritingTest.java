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
 * Klasa LibraryWritingTest testuje metodę writing() w klasie Library,
 * symulując scenariusze związane z jednoczesnym dostępem kilku pisarzy do czytelni.
 */
@RunWith(PowerMockRunner.class)
class LibraryWritingTest extends MultithreadedTest {
    private Library library;
    private Semaphore wrtMock;
    private Writer writer1;
    private Writer writer2;

    /**
     * Metoda inicjalizująca środowisko testowe.
     * Tworzy mocki dla semaforów, inicjalizuje czytelnię oraz dwóch pisarzy.
     */
    @Override
    public void initialize() {
        wrtMock = PowerMockito.mock(Semaphore.class);
        Semaphore mutexMock = PowerMockito.mock(Semaphore.class);
        writer1 = new Writer(1, library);
        writer2 = new Writer(2, library);

        library = new Library(wrtMock, mutexMock);
    }

    /**
     * Symuluje scenariusz, w którym pierwszy pisarz wykonuje operację pisania.
     *
     * @throws InterruptedException jeśli operacja jest przerwana.
     */
    void thread1() throws InterruptedException {
        library.writing(writer1);
    }

    /**
     * Symuluje scenariusz, w którym drugi pisarz wykonuje operację pisania.
     *
     * @throws InterruptedException jeśli operacja jest przerwana.
     */
    void thread2() throws InterruptedException {
        library.writing(writer2);
    }

    /**
     * Testuje poprawność działania metody writing() w kontekście współbieżności.
     * Sprawdza, czy lista pisarzy w bibliotece jest pusta po zakończeniu operacji
     * oraz czy semafor wrt został prawidłowo użyty (zablokowany i zwolniony).
     *
     * @throws Throwable jeśli wystąpi błąd podczas testu.
     */
    @Test
    void testWriting() throws Throwable {
        initialize();
        thread1();
        thread2();

        Assertions.assertEquals(0, library.getWritersInLibrary().size());

        try {
            verify(wrtMock, times(2)).acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        verify(wrtMock, times(2)).release();
    }
}
