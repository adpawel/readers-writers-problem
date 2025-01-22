package pl.edu.agh.kis.pz1;

import lombok.Data;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Klasa Reader reprezentuje czytelnika jako wątek w symulacji.
 * Każdy czytelnik posiada unikalny identyfikator i odniesienie do czytelni, w której działa.
 */
@Data
public class Reader extends Thread {
    private final Integer readerId;
    private final Library library;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private volatile boolean running = true;
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * Konstruktor inicjalizuje czytelnika z podanym identyfikatorem i odniesieniem do czytelni.
     *
     * @param id       unikalny identyfikator czytelnika.
     * @param library  czytelnia, z której czytelnik korzysta.
     */
    public Reader(Integer id, Library library) {
        System.out.println("Czytelnik " + id + " wystartował");
        this.readerId = id;
        this.library = library;
    }

    /**
     * Główna metoda wątku, obsługuje działania czytelnika podczas symulacji.
     * Czytelnik wchodzi do czytelni, czyta, a następnie czeka przez losowy czas.
     */
    @Override
    public void run() {
        try {
            latch.countDown();
            while(running){
                library.reading(this);
                Thread.sleep(random.nextInt(500, 2000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Wątek czytelnika " + readerId + " przerwany");
        }
    }

    /**
     * Porównuje tego czytelnika z innym obiektem na podstawie identyfikatora.
     *
     * @param o obiekt do porównania.
     * @return true, jeśli identyfikatory są takie same; false w przeciwnym razie.
     */
    @Override
    public boolean equals(Object o){
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        Reader other = (Reader) o;
        return Objects.equals(other.getReaderId(), readerId);
    }

    /**
     * Generuje hash code na podstawie identyfikatora czytelnika.
     *
     * @return hash code czytelnika.
     */
    @Override
    public int hashCode() {
        return Objects.hash(readerId);
    }

    /**
     * Zatrzymuje działanie wątku czytelnika.
     */
    public void stopRunning() {
        running = false;
    }
}


