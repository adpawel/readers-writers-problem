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
class LibraryStartWritingTest extends MultithreadedTest {
    private Library library;
    private Semaphore wrtMock;

    @Override
    public void initialize() {
        wrtMock = PowerMockito.mock(Semaphore.class);
        Semaphore mutexMock = PowerMockito.mock(Semaphore.class);

        library = new Library(wrtMock, mutexMock);
    }

    void thread1() throws InterruptedException {
        library.startWriting(new Writer(1, library));
    }

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
