package pl.edu.agh.kis.pz1;

import lombok.Data;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class Reader extends Thread {
    private final Integer readerId;
    private final Library library;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private volatile boolean running = true;
    private CountDownLatch latch = new CountDownLatch(1);

    public Reader(Integer id, Library library) {
        System.out.println("Czytelnik " + id + " wystartował");
        this.readerId = id;
        this.library = library;
    }

    @Override
    public void run() {
        try {
            latch.countDown();
            while(running){
                library.reading(this);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Wątek czytelnika " + readerId + " przerwany");
        }
    }

    @Override
    public boolean equals(Object o){
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        Reader other = (Reader) o;
        return Objects.equals(other.getReaderId(), readerId);
    }

    @Override
    public int hashCode() {
        int result = readerId.hashCode();
        result = 31 * result + random.nextInt(1, 10);
        return result;
    }

    public void stopRunning() {
        running = false;
    }
}


