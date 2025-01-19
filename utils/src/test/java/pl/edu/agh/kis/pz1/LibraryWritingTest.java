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
class LibraryWritingTest extends MultithreadedTest {
    private Library library;
    private Semaphore wrtMock;
    private Writer writer1;
    private Writer writer2;

    @Override
    public void initialize() {
        wrtMock = PowerMockito.mock(Semaphore.class);
        Semaphore mutexMock = PowerMockito.mock(Semaphore.class);
        writer1 = new Writer(1, library);
        writer2 = new Writer(2, library);

        library = new Library(wrtMock, mutexMock);
    }

    void thread1() throws InterruptedException {
        library.writing(writer1);
    }

    void thread2() throws InterruptedException {
        library.writing(writer2);
    }

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
