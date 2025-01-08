package pl.edu.agh.kis.pz1;

import lombok.Getter;

@Getter
public class Writer extends Thread{
    private final Integer writerId;
    private final Library library;

    public Writer(Integer id, Library library) {
        System.out.println("Pisarz " + id + " wystartował");
        this.writerId = id;
        this.library = library;
    }

    @Override
    public void run() {
        try {
            Thread.sleep((int) (Math.random() + 0.2) * 1000);
            while (true) {
                library.writing(this);
            }
        } catch (InterruptedException e) {
            System.err.println("Wątek pisarza " + writerId + " przerwany");
        }
    }
}
