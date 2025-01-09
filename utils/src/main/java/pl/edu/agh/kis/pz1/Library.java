package pl.edu.agh.kis.pz1;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Getter
public class Library {
    private int readerCount = 0;
    private final Semaphore rdr = new Semaphore(5, true);
    private final Semaphore mutex = new Semaphore(1, true);
    private final Semaphore wrt = new Semaphore(1, true);
    private final int noAllowedReaders;
    private List<Reader> readersInLibrary = new ArrayList<>();
    private List<Writer> writersInLibrary = new ArrayList<>();
    private final Random random = new Random();
    private List<Thread> queue = new ArrayList<>();

    public Library() {
        this.noAllowedReaders = 5;
    }

    public Library(int noAllowedReaders) {
        this.noAllowedReaders = noAllowedReaders;
    }

    void reading(Reader reader) throws InterruptedException {
        sendCommunicate("Czytelnik " + reader.getReaderId() + " chce wejść do czytleni");
        rdr.acquire();
        mutex.acquire();
//        if( maxNumberOfReadersReached() ) {
//            mutex.release();
//            return;
//        }
        startReading(reader);
        Thread.sleep(random.nextInt(1000, 3000)); // czytanie
        stopReading(reader);
        Thread.sleep(random.nextInt(5000, 7000)); // czekanie
//        Thread.sleep(random.nextInt(1500, 4500));
    }

    void startReading(Reader reader) throws InterruptedException {
        readerCount++;
        if(readerCount == 1){
            wrt.acquire();
        }
        readersInLibrary.add(reader);
        sendCommunicate("Czytelnik " + reader.getReaderId() + " czyta...");
        logWhoIsInLibrary();
        mutex.release();
    }

    private void stopReading(Reader reader) throws InterruptedException {
        mutex.acquire();
        readerCount--;
        readersInLibrary = readersInLibrary.stream().filter(r -> !r.equals(reader)).collect(Collectors.toList());
        sendCommunicate("Czytelnik " + reader.getReaderId() + " skończył czytać.");
        logWhoIsInLibrary();
        if (isEmpty()) {
            wrt.release();
        }
        mutex.release();

        rdr.release();
    }

    private void logWhoIsInLibrary(){
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("W czytelni: ");
        readersInLibrary
                .forEach(r -> messageBuilder.append("Czytelnik ").append(r.getReaderId()).append(", "));
        writersInLibrary
                .forEach(w -> messageBuilder.append("Pisarz ").append(w.getWriterId()).append(", "));
        sendCommunicate(messageBuilder.toString());
    }

    void writing(Writer writer) throws InterruptedException {
        sendCommunicate("Pisarz " + writer.getWriterId() + " chce wejść do czytelni.");
        startWriting(writer);

        Thread.sleep(random.nextInt(1000, 3000));

        stopWriting(writer);

        Thread.sleep(random.nextInt(1500, 4500));
    }

    private void startWriting(Writer writer) throws InterruptedException {
        wrt.acquire();
        sendCommunicate("Pisarz " + writer.getWriterId() + " pisze.");
        writersInLibrary.add(writer);
        logWhoIsInLibrary();
    }

    private void stopWriting(Writer writer) {
        writersInLibrary = writersInLibrary.stream().filter(w -> !w.equals(writer)).collect(Collectors.toList());
        sendCommunicate("Pisarz " + writer.getWriterId() + " skończył pisać.");
        logWhoIsInLibrary();
        wrt.release();
    }

    private void sendCommunicate(String communicate){
        System.out.println(communicate);
    }

    private boolean isEmpty(){
        return readerCount == 0;
    }

    private boolean maxNumberOfReadersReached(){
        return readerCount >= noAllowedReaders;
    }
}
