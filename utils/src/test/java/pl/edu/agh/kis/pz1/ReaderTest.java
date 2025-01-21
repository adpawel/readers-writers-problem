package pl.edu.agh.kis.pz1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Klasa ReaderTest zawiera testy jednostkowe dla klasy Reader.
 * Testuje porównywanie obiektów, poprawność działania metody run() oraz zachowanie metody hashCode().
 */
class ReaderTest {
    private Library libraryMock;
    private Reader r1;
    private Reader r2;
    private Reader r3;

    /**
     * Inicjalizuje obiekty potrzebne do każdego testu.
     * Tworzy mock klasy Library oraz kilka instancji klasy Reader.
     */
    @BeforeEach
    void setUp() {
        libraryMock = PowerMockito.mock(Library.class);
        r1 = new Reader(1, libraryMock);
        r2 = new Reader(1, libraryMock);
        r3 = new Reader(3, libraryMock);
    }

    /**
     * Testuje metodę equals() w klasie Reader.
     * Sprawdza, czy obiekty z tym samym identyfikatorem są równe,
     * a obiekty z różnymi identyfikatorami są różne.
     */
    @Test
    void equalsTest(){
        Assertions.assertEquals(r1, r2);
        Assertions.assertNotEquals(r1, r3);
        Assertions.assertNotEquals(r2, r3);
        Assertions.assertNotEquals(null, r1);
        Assertions.assertNotEquals("string", r1);
    }

    /**
     * Testuje metodę run() w klasie Reader.
     * Symuluje działanie wątku, sprawdzając, czy metoda reading() w mocku Library
     * została wywołana przynajmniej raz.
     *
     * @throws InterruptedException w przypadku problemów z synchronizacją wątku.
     */
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

    /**
     * Testuje, czy metoda hashCode() zwraca ten sam wynik
     * dla tego samego obiektu Reader.
     */
    @Test
    void testConsistentHashCode() {
        Reader reader = new Reader(1, libraryMock);
        int hashCode1 = reader.hashCode();
        int hashCode2 = reader.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    /**
     * Testuje, czy dwa równe obiekty Reader mają taki sam hashCode.
     */
    @Test
    void testEqualObjectsHaveSameHashCode() {
        Reader reader1 = new Reader(1, libraryMock);
        Reader reader2 = new Reader(1, libraryMock);

        assertEquals(reader1.hashCode(), reader2.hashCode());
    }

    /**
     * Testuje, czy dwa różne obiekty Reader mają różne hashCode.
     */
    @Test
    void testUnequalObjectsHaveDifferentHashCodes() {
        Reader reader1 = new Reader(1, libraryMock);
        Reader reader2 = new Reader(2, libraryMock);

        assertNotEquals(reader1.hashCode(), reader2.hashCode());
    }
}