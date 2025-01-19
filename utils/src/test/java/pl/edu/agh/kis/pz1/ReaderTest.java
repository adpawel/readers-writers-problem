package pl.edu.agh.kis.pz1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.api.mockito.PowerMockito;

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
        Assertions.assertNotEquals(null, r1);
        Assertions.assertNotEquals("string", r1);
    }

    @Test
    void testRun() throws InterruptedException {
        Thread thread = new Thread(r1);
        thread.start();
        verify(libraryMock, atLeastOnce()).reading(r1);
        r1.stopRunning();
        thread.join();
    }
}