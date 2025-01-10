package pl.edu.agh.kis.pz1;

import lombok.Getter;

import java.util.Objects;
import java.util.Random;

@Getter
public class Reader extends Thread {
    private final Integer readerId;
    private final Library library;
    private final Random random = new Random();

    public Reader(Integer id, Library library) {
        System.out.println("Czytelnik " + id + " wystartował");
        this.readerId = id;
        this.library = library;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(random.nextInt(200, 1200));
            while(true){
                library.reading(this);
            }
        } catch (InterruptedException e) {
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
}


