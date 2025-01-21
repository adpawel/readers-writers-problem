package pl.edu.agh.kis.pz1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Klasa WriterTest zawiera testy jednostkowe dla klasy Writer.
 * Testuje porównywanie obiektów, poprawność działania metody run() oraz zachowanie metody hashCode().
 */
class WriterTest {
    private Library libraryMock;
    private Writer w1;
    private Writer w2;
    private Writer w3;

    /**
     * Inicjalizuje obiekty potrzebne do każdego testu.
     * Tworzy mock klasy Library oraz kilka instancji klasy Writer.
     */
    @BeforeEach
    void setUp() {
        libraryMock = PowerMockito.mock(Library.class);
        w1 = new Writer(1, libraryMock);
        w2 = new Writer(1, libraryMock);
        w3 = new Writer(3, libraryMock);
    }

    /**
     * Testuje metodę equals() w klasie Writer.
     * Sprawdza, czy obiekty z tym samym identyfikatorem są równe,
     * a obiekty z różnymi identyfikatorami są różne.
     */
    @Test
    void equalsTest(){
        assertEquals(w1, w2);
        assertNotEquals(w1, w3);
        assertNotEquals(w2, w3);
        assertNotEquals(null, w1);
        assertNotEquals("string", w1);
    }

    /**
     * Testuje metodę run() w klasie Writer.
     * Symuluje działanie wątku, sprawdzając, czy metoda writing() w mocku Library
     * została wywołana przynajmniej raz.
     *
     * @throws InterruptedException w przypadku problemów z synchronizacją wątku.
     */
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

    /**
     * Testuje, czy metoda hashCode() zwraca ten sam wynik
     * dla tego samego obiektu Writer.
     */
    @Test
    void testConsistentHashCode() {
        Writer writer = new Writer(1, libraryMock);
        int hashCode1 = writer.hashCode();
        int hashCode2 = writer.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    /**
     * Testuje, czy dwa równe obiekty Writer mają taki sam hashCode.
     */
    @Test
    void testEqualObjectsHaveSameHashCode() {
        Writer writer1 = new Writer(1, libraryMock);
        Writer writer2 = new Writer(1, libraryMock);

        assertEquals(writer1.hashCode(), writer2.hashCode());
    }

    /**
     * Testuje, czy dwa różne obiekty Writer mają różne hashCode.
     */
    @Test
    void testUnequalObjectsHaveDifferentHashCodes() {
        Writer writer1 = new Writer(1, libraryMock);
        Writer writer2 = new Writer(2, libraryMock);

        assertNotEquals(writer1.hashCode(), writer2.hashCode());
    }
}