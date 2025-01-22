package pl.edu.agh.kis.pz1;

import lombok.Data;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Klasa Writer reprezentuje pisarza jako wątek w symulacji.
 * Każdy pisarz posiada unikalny identyfikator i odniesienie do czytelni, w której działa.
 */
@Data
public class Writer extends Thread{
    private final Integer writerId;
    private final Library library;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private volatile boolean running = true;
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * Konstruktor inicjalizuje pisarza z podanym identyfikatorem i odniesieniem do czytelni.
     *
     * @param id       unikalny identyfikator pisarza.
     * @param library  czytelnia, z której pisarz korzysta.
     */
    public Writer(Integer id, Library library) {
        System.out.println("Pisarz " + id + " wystartował");
        this.writerId = id;
        this.library = library;
    }

    /**
     * Główna metoda wątku, obsługuje działania pisarza podczas symulacji.
     * Pisarz wchodzi do czytelni, pisze, a następnie czeka przez losowy czas.
     */
    @Override
    public void run() {
        try {
            latch.countDown();
            while (running) {
                library.writing(this);
                Thread.sleep(random.nextInt(500, 2000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Wątek pisarza " + writerId + " przerwany");
        }
    }

    /**
     * Porównuje tego pisarza z innym obiektem na podstawie identyfikatora.
     *
     * @param o obiekt do porównania.
     * @return true, jeśli identyfikatory są takie same; false w przeciwnym razie.
     */
    @Override
    public boolean equals(Object o){
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        Writer other = (Writer) o;
        return Objects.equals(other.getWriterId(), writerId);
    }

    /**
     * Generuje hash code na podstawie identyfikatora pisarza.
     *
     * @return hash code pisarza.
     */
    @Override
    public int hashCode() {
        return Objects.hash(writerId);
    }

    /**
     * Zatrzymuje działanie wątku pisarza.
     */
    public void stopRunning() {
        running = false;
    }
}
