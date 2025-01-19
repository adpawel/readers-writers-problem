package pl.edu.agh.kis.pz1;

import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Writer extends Thread{
    private final Integer writerId;
    private final Library library;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private volatile boolean running = true;


    public Writer(Integer id, Library library) {
        System.out.println("Pisarz " + id + " wystartował");
        this.writerId = id;
        this.library = library;
    }

    @Override
    public void run() {
        try {
            while (running) {
//            Thread.sleep(random.nextInt(200, 800));
                library.writing(this);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Wątek pisarza " + writerId + " przerwany");
        }
    }

    @Override
    public boolean equals(Object o){
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        Writer other = (Writer) o;
        return Objects.equals(other.getWriterId(), writerId);
    }

    @Override
    public int hashCode() {
        int result = writerId.hashCode();
        result = 31 * result + random.nextInt(1, 10);
        return result;
    }

    public void stopRunning() {
        running = false;
    }
}
