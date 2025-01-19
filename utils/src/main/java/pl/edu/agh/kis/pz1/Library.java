package pl.edu.agh.kis.pz1;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;


@Getter
public class Library {
    private int readerCount = 0;
    private final Semaphore mutex;
    private final Semaphore wrt;
    private final int noAllowedReaders;
    private List<Reader> readersInLibrary = new ArrayList<>();
    private List<Writer> writersInLibrary = new ArrayList<>();
    private final Random random = new Random();
    private final List<Thread> requestQueue = new LinkedList<>();

    public Library() {
        mutex = new Semaphore(1, true);
        wrt = new Semaphore(1, true);
        this.noAllowedReaders = 5;
    }

    public Library(int noAllowedReaders) {
        mutex = new Semaphore(1, true);
        wrt = new Semaphore(1, true);
        this.noAllowedReaders = noAllowedReaders;
    }

    public Library(Semaphore wrt, Semaphore mutex) {
        this.noAllowedReaders = 5;
        this.wrt = wrt;
        this.mutex = mutex;
    }

    void reading(Reader reader) throws InterruptedException {
        sendCommunicate("Czytelnik " + reader.getReaderId() + " chce wejść do czytelni");

        synchronized (this) {
            requestQueue.add(Thread.currentThread());
            while (!Thread.currentThread().equals(requestQueue.get(0)) || readerCount >= noAllowedReaders) {
                wait();
            }
        }

        mutex.acquire();
        startReading(reader);
        mutex.release();

        Thread.sleep(random.nextInt(1000, 3000)); // czytanie
//        mutex.acquire();
        stopReading(reader);
//        mutex.release();
    }

    void startReading(Reader reader) throws InterruptedException {
//        synchronized (this) {
            readerCount++;
            if (readerCount == 1) {
                wrt.acquire();
            }
            readersInLibrary.add(reader);
            sendCommunicate("Czytelnik " + reader.getReaderId() + " czyta...");
            logWhoIsInLibrary();
//        }
        synchronized (this) {
            requestQueue.remove(Thread.currentThread());
            notifyAll();
        }
    }

    void stopReading(Reader reader) throws InterruptedException {
        mutex.acquire();
            readerCount--;
            readersInLibrary = readersInLibrary.stream().filter(r -> !r.equals(reader)).collect(Collectors.toList());
            sendCommunicate("Czytelnik " + reader.getReaderId() + " skończył czytać.");
            logWhoIsInLibrary();
            if (readerCount == 0) {
                wrt.release();
            }
        mutex.release();
        synchronized (this) {
            notifyAll();
        }
    }

    void logWhoIsInLibrary() {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("W czytelni: ");
        readersInLibrary.forEach(r -> messageBuilder.append("Czytelnik ").append(r.getReaderId()).append(", "));
        writersInLibrary.forEach(w -> messageBuilder.append("Pisarz ").append(w.getWriterId()).append(", "));
        sendCommunicate(messageBuilder.toString());
    }

    void writing(Writer writer) throws InterruptedException {
        sendCommunicate("Pisarz " + writer.getWriterId() + " chce wejść do czytelni");

        synchronized (this) {
            requestQueue.add(Thread.currentThread());
            while (!Thread.currentThread().equals(requestQueue.get(0))) {
                wait();
            }
        }

        startWriting(writer);

        Thread.sleep(random.nextInt(1000, 3000)); // pisanie

        stopWriting(writer);
    }

    void startWriting(Writer writer) throws InterruptedException {
        wrt.acquire();
        sendCommunicate("Pisarz " + writer.getWriterId() + " pisze.");
        writersInLibrary.add(writer);
        logWhoIsInLibrary();
    }

    void stopWriting(Writer writer) throws InterruptedException {
        writersInLibrary = writersInLibrary.stream().filter(w -> !w.equals(writer)).collect(Collectors.toList());
        sendCommunicate("Pisarz " + writer.getWriterId() + " skończył pisać.");
        logWhoIsInLibrary();
        wrt.release();
        synchronized (this) {
            requestQueue.remove(Thread.currentThread());
            notifyAll();
        }
    }

    void sendCommunicate(String communicate) {
        System.out.println(communicate);
    }
}
