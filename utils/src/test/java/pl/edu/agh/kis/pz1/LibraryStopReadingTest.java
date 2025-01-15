package pl.edu.agh.kis.pz1;

import edu.umd.cs.mtc.MultithreadedTest;
import edu.umd.cs.mtc.TestFramework;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.Semaphore;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
class LibraryStopReadingTest extends MultithreadedTest {
    private Library library;
    private Semaphore wrtMock;
    private Semaphore mutexMock;
    private Reader reader1;
    private Reader reader2;

    @Override
    public void initialize() {
        wrtMock = PowerMockito.mock(Semaphore.class);
        mutexMock = PowerMockito.mock(Semaphore.class);
        reader1 = new Reader(1, library);
        reader2 = new Reader(2, library);

        library = new Library(wrtMock, mutexMock);
    }

    void thread1() throws InterruptedException {
        library.startReading(reader1);
        Thread.sleep(100);
        library.stopReading(reader1);
    }

    void thread2() throws InterruptedException {
        library.startReading(reader2);
        library.stopReading(reader2);
    }

    @Override
    public void finish() {
        Assertions.assertEquals(0, library.getReaderCount());
        Assertions.assertEquals(0, library.getReadersInLibrary().size());

        try {
            verify(wrtMock, times(1)).acquire();
            verify(mutexMock, times(2)).acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        verify(mutexMock, times(4)).release();      // dwa razy w startReading i dwa razy w stopReading
        verify(wrtMock, times(1)).release();
    }

    @Test
    void testStopReading() throws Throwable {
        TestFramework.runOnce(new LibraryStopReadingTest());
    }
}