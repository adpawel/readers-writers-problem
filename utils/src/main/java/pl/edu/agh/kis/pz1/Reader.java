package pl.edu.agh.kis.pz1;

import lombok.Getter;

@Getter
public class Reader extends Thread {
    private final Integer readerId;
    private Library library;

    public Reader(Integer id, Library library) {
        System.out.println("Czytelnik " + id + " wystartował");
        this.readerId = id;
        this.library = library;
    }

    @Override
    public void run() {
        try {
            Thread.sleep((int) (Math.random() + 0.2) * 1000);
            while(true){
                library.startReading(this);
            }
        } catch (InterruptedException e) {
            System.err.println("Wątek czytelnika " + readerId + " przerwany");
        }
    }
}
