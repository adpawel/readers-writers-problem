package pl.edu.agh.kis.pz1;

import lombok.Getter;

import java.util.Objects;
import java.util.Random;

@Getter
public class Writer extends Thread{
    private final Integer writerId;
    private final Library library;
    private final Random random = new Random();

    public Writer(Integer id, Library library) {
        System.out.println("Pisarz " + id + " wystartował");
        this.writerId = id;
        this.library = library;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(random.nextInt(200, 1200));
            while (true) {
                library.writing(this);
            }
        } catch (InterruptedException e) {
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
}
