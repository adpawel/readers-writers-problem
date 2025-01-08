package pl.edu.agh.kis.pz1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

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

    void reading(Reader reader) throws InterruptedException {
        mutex.acquire();
        if( maxNumberOfReadersReached() ) {
            mutex.release();
            return;
        }

        startReading(reader);
        Thread.sleep((int) ((Math.random() + 0.5) * 2000)); // czytanie
        stopReading(reader);
        Thread.sleep((int) ((Math.random() + 0.5) * 3000)); // czekanie
    }

    private void startReading(Reader reader) throws InterruptedException {
        readersInLibrary.add(reader);
        readerCount++;
        if(readerCount == 1){
            wrt.acquire();
        }
        sendCommunicate("Czytelnik " + reader.getReaderId() + " czyta...");
        logReadersInLibrary();
        mutex.release();
    }

    private void stopReading(Reader reader) throws InterruptedException {
        mutex.acquire();
        readerCount--;
        readersInLibrary = readersInLibrary.stream().filter(r -> r.getReaderId() != reader.getReaderId()).collect(Collectors.toList());
        sendCommunicate("Czytelnik " + reader.getReaderId() + " skończył czytać.");
        logReadersInLibrary();
        if (isEmpty()) {
            wrt.release();
        }
        mutex.release();
    }

    private void logReadersInLibrary(){
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Czytelnicy w czytelni: ");
        readersInLibrary
                .forEach(r -> messageBuilder.append(r.getReaderId()).append(" "));
        sendCommunicate(messageBuilder.toString());
    }

    void writing(Writer writer) throws InterruptedException {
        sendCommunicate("Pisarz " + writer.getWriterId() + " chce wejść do czytelni.");
        startWriting(writer);

        Thread.sleep((int) ((Math.random() + 0.5) * 2000));

        stopWriting(writer);

        Thread.sleep((int) ((Math.random() + 0.5) * 3000));
    }

    private void startWriting(Writer writer) throws InterruptedException {
        wrt.acquire();
        sendCommunicate("Pisarz " + writer.getWriterId() + " pisze.");
        writersInLibrary.add(writer);
        logWritersInLibrary();
    }

    private void stopWriting(Writer writer) throws InterruptedException {
        writersInLibrary = writersInLibrary.stream().filter(w -> w.getWriterId() != writer.getWriterId()).collect(Collectors.toList());
        sendCommunicate("Pisarz " + writer.getWriterId() + " skończył pisać.");
        logWritersInLibrary();
        wrt.release();
    }

    private void logWritersInLibrary(){
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Pisarze w czytelni: ");
        writersInLibrary
                .forEach(w -> messageBuilder.append(w.getWriterId()).append(" "));
        sendCommunicate(messageBuilder.toString());
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
