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
class LibraryStopWritingTest extends MultithreadedTest {
    private Library library;
    private Semaphore wrtMock;
    private Writer writer;

    @Override
    public void initialize() {
        wrtMock = PowerMockito.mock(Semaphore.class);
        Semaphore mutexMock = PowerMockito.mock(Semaphore.class);
        writer = new Writer(1, library);

        library = new Library(wrtMock, mutexMock);
    }

    void thread1() throws InterruptedException {
        library.startWriting(writer);
        Thread.sleep(100);
        library.stopWriting(writer);
    }

    @Override
    public void finish() {
        Assertions.assertEquals(0, library.getWritersInLibrary().size());

        verify(wrtMock, times(1)).release();
    }

    @Test
    void testStopWriting() throws Throwable {
        TestFramework.runOnce(new LibraryStopWritingTest());
    }
}