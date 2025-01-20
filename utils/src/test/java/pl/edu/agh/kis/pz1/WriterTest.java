package pl.edu.agh.kis.pz1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

class WriterTest {
    private Library libraryMock;
    private Writer w1;
    private Writer w2;
    private Writer w3;

    @BeforeEach
    void setUp() {
        libraryMock = PowerMockito.mock(Library.class);
        w1 = new Writer(1, libraryMock);
        w2 = new Writer(1, libraryMock);
        w3 = new Writer(3, libraryMock);
    }

    @Test
    void equalsTest(){
        Assertions.assertEquals(w1, w2);
        Assertions.assertNotEquals(w1, w3);
        Assertions.assertNotEquals(w2, w3);
        Assertions.assertFalse(w1.equals(null));
        Assertions.assertFalse(w1.equals("string"));
    }

    @Test
    void testRun() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        w1.setLatch(latch);
        w1.start();
        latch.await();

        w1.stopRunning();
        w1.join();
        verify(libraryMock, atLeastOnce()).writing(w1);
    }
}