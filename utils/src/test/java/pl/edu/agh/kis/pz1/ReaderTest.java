package pl.edu.agh.kis.pz1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.*;

class ReaderTest {
    private Library libraryMock;
    private Reader r1;
    private Reader r2;
    private Reader r3;

    @BeforeEach
    void setUp() {
        libraryMock = PowerMockito.mock(Library.class);
        r1 = new Reader(1, libraryMock);
        r2 = new Reader(1, libraryMock);
        r3 = new Reader(3, libraryMock);
    }

    @Test
    void equalsTest(){
        Assertions.assertEquals(r1, r2);
        Assertions.assertNotEquals(r1, r3);
        Assertions.assertNotEquals(r2, r3);
        Assertions.assertFalse(r1.equals(null));
        Assertions.assertFalse(r1.equals("string"));
    }

    @Test
    void testRun() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        r1.setLatch(latch);
        r1.start();
        latch.await();

        r1.stopRunning();
        r1.join();
        verify(libraryMock, atLeastOnce()).reading(r1);
    }
}