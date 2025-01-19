package pl.edu.agh.kis.pz1;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@Getter
public class Library {
    private static final String READER_CONST = "Czytelnik ";
    private static final String WRITER_CONST = "Pisarz ";
    private int readerCount = 0;
    private final Semaphore mutex;
    private final Semaphore wrt;
    private final int noAllowedReaders;
    private List<Reader> readersInLibrary = new ArrayList<>();
    private List<Writer> writersInLibrary = new ArrayList<>();
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final Queue<Thread> requestQueue = new LinkedList<>();

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
        sendCommunicate(READER_CONST + reader.getReaderId() + " chce wejść do czytelni");

        synchronized (this) {
            requestQueue.add(Thread.currentThread());
            logWhoIsInQueue();
            while (!Thread.currentThread().equals(requestQueue.peek()) || readerCount >= noAllowedReaders) {
                wait();
            }
        }

        startReading(reader);
        Thread.sleep(random.nextInt(1000, 3000)); // czytanie
        stopReading(reader);
    }

    void startReading(Reader reader) throws InterruptedException {
        mutex.acquire();
        readerCount++;
        if (readerCount == 1) {
            wrt.acquire();
        }
        readersInLibrary.add(reader);
        sendCommunicate(READER_CONST + reader.getReaderId() + " czyta...");
        logWhoIsInLibrary();
        synchronized (this) {
            requestQueue.remove(Thread.currentThread());
            notifyAll();
        }
        mutex.release();
    }

    void stopReading(Reader reader) throws InterruptedException {
        mutex.acquire();
        readerCount--;
        readersInLibrary = readersInLibrary.stream().filter(r -> !r.equals(reader)).collect(Collectors.toList());
        sendCommunicate(READER_CONST + reader.getReaderId() + " skończył czytać.");
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
        readersInLibrary.forEach(r -> messageBuilder.append(READER_CONST).append(r.getReaderId()).append(", "));
        writersInLibrary.forEach(w -> messageBuilder.append(WRITER_CONST).append(w.getWriterId()).append(", "));
        sendCommunicate(messageBuilder.toString());
    }

    void logWhoIsInQueue() {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("W kolejce: ");

        for (Thread thread : requestQueue) {
            if (thread instanceof Reader reader) {
                messageBuilder.append(READER_CONST).append(reader.getReaderId()).append(", ");
            } else if (thread instanceof Writer writer) {
                messageBuilder.append(WRITER_CONST).append(writer.getWriterId()).append(", ");
            }
        }
        sendCommunicate(messageBuilder.toString());
    }


    void writing(Writer writer) throws InterruptedException {
        sendCommunicate(WRITER_CONST + writer.getWriterId() + " chce wejść do czytelni");

        synchronized (this) {
            requestQueue.add(Thread.currentThread());
            logWhoIsInQueue();
            while (!Thread.currentThread().equals(requestQueue.peek())) {
                wait();
            }
        }

        startWriting(writer);
        Thread.sleep(random.nextInt(1000, 3000)); // pisanie
        stopWriting(writer);
    }

    void startWriting(Writer writer) throws InterruptedException {
        wrt.acquire();
        sendCommunicate(WRITER_CONST + writer.getWriterId() + " pisze.");
        writersInLibrary.add(writer);
        logWhoIsInLibrary();
    }

    void stopWriting(Writer writer) {
        writersInLibrary = writersInLibrary.stream().filter(w -> !w.equals(writer)).collect(Collectors.toList());
        sendCommunicate(WRITER_CONST + writer.getWriterId() + " skończył pisać.");
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
