package pl.edu.agh.kis.pz1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Library {
    private int readerCount = 0;
    private final Semaphore mutex = new Semaphore(1, true);
    private final Semaphore wrt = new Semaphore(1, true);
    private int noAllowedReaders = 5;
    private List<Reader> readersInLibrary = new ArrayList<>();
    private List<Writer> writersInLibrary = new ArrayList<>();

    public Library() {}

    public Library(int noAllowedReaders) {
        this.noAllowedReaders = noAllowedReaders;
    }

    void startReading(Reader reader) throws InterruptedException {
        mutex.acquire();
        if( !maxNumberOfReadersReached() ) {

        }
        else{
            mutex.release();
        }

    }

    void writing(Writer writer){

    }

    private void sendCommunicate(String communicate){
        System.out.println(communicate);
    }

    private boolean maxNumberOfReadersReached(){
        return readerCount >= noAllowedReaders;
    }
}
