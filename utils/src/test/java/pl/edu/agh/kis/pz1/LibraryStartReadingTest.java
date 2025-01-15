package pl.edu.agh.kis.pz1;

import edu.umd.cs.mtc.MultithreadedTest;
import edu.umd.cs.mtc.TestFramework;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.Semaphore;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
class LibraryStartReadingTest extends MultithreadedTest {
    private Library library;
    private Semaphore wrtMock;
    private Semaphore mutexMock;

    @Override
    public void initialize() {
        wrtMock = PowerMockito.mock(Semaphore.class);
        mutexMock = PowerMockito.mock(Semaphore.class);

        library = new Library(wrtMock, mutexMock);
    }

    void thread1() throws InterruptedException {
        library.startReading(new Reader(1, library));
    }

    void thread2() throws InterruptedException {
        library.startReading(new Reader(2, library));
    }

    @Override
    public void finish() {
        Assertions.assertEquals(2, library.getReaderCount());
        Assertions.assertEquals(2, library.getReadersInLibrary().size());

        try {
            verify(wrtMock, times(1)).acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        verify(mutexMock, times(2)).release();
    }

    @Test
    void testStartReading() throws Throwable {
        TestFramework.runOnce(new LibraryStartReadingTest());
    }
}