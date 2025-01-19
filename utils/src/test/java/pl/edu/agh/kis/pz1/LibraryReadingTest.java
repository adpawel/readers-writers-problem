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

@RunWith(PowerMockRunner.class)
class LibraryReadingTest extends MultithreadedTest {
    private Library library;
    private Semaphore wrtMock;
    private Semaphore mutexMock;
    private Reader reader1;

    @Override
    public void initialize() {
        wrtMock = PowerMockito.mock(Semaphore.class);
        mutexMock = PowerMockito.mock(Semaphore.class);
        reader1 = new Reader(1, library);

        library = new Library(wrtMock, mutexMock);
    }

    void thread1() throws InterruptedException {
        library.reading(reader1);
    }

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

        verify(mutexMock, times(2)).release();      // dwa razy w startReading i dwa razy w stopReading
        verify(wrtMock, times(1)).release();
    }
}