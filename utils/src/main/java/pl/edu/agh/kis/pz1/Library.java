package pl.edu.agh.kis.pz1;

import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Klasa Library reprezentuje bibliotekę, w której czytelnicy i pisarze współdzielą zasoby.
 * Zarządza procesem wchodzenia, czytania, pisania i wychodzenia z czytelni.
 */
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

    /**
     * Konstruktor domyślny. Ustawia domyślne limity dla liczby czytelników.
     */
    public Library() {
        mutex = new Semaphore(1);
        wrt = new Semaphore(1);
        this.noAllowedReaders = 5;
    }

    /**
     * Konstruktor z możliwością ustawienia limitu liczby czytelników.
     *
     * @param noAllowedReaders maksymalna liczba czytelników dozwolona w czytelni.
     */
    public Library(int noAllowedReaders) {
        mutex = new Semaphore(1, true);
        wrt = new Semaphore(1, true);
        this.noAllowedReaders = noAllowedReaders;
    }

    /**
     * Konstruktor z przekazaniem semaforów.
     *
     * @param wrt   semafor współdzielony.
     * @param mutex semafor dla czytelników.
     */
    public Library(Semaphore wrt, Semaphore mutex) {
        this.noAllowedReaders = 5;
        this.wrt = wrt;
        this.mutex = mutex;
    }

    /**
     * Metoda obsługuje proces czytania dla podanego czytelnika.
     *
     * @param reader obiekt czytelnika.
     * @throws InterruptedException w przypadku przerwania wątku.
     */
    void reading(Reader reader) throws InterruptedException {
        synchronized (this) {
            sendCommunicate(READER_CONST + reader.getReaderId() + " chce wejść do czytelni");
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

    /**
     * Rozpoczyna proces czytania dla podanego czytelnika.
     *
     * @param reader obiekt czytelnika.
     * @throws InterruptedException w przypadku przerwania wątku.
     */
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

    /**
     * Kończy proces czytania dla podanego czytelnika.
     *
     * @param reader obiekt czytelnika.
     * @throws InterruptedException w przypadku przerwania wątku.
     */
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

    /**
     * Metoda obsługuje proces pisania dla podanego pisarza.
     *
     * @param writer obiekt pisarza.
     * @throws InterruptedException w przypadku przerwania wątku.
     */
    void writing(Writer writer) throws InterruptedException {
        synchronized (this) {
            sendCommunicate(WRITER_CONST + writer.getWriterId() + " chce wejść do czytelni");
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

    /**
     * Rozpoczyna proces pisania dla podanego pisarza.
     *
     * @param writer obiekt pisarza.
     * @throws InterruptedException w przypadku przerwania wątku.
     */
    void startWriting(Writer writer) throws InterruptedException {
        wrt.acquire();
        sendCommunicate(WRITER_CONST + writer.getWriterId() + " pisze.");
        writersInLibrary.add(writer);
        logWhoIsInLibrary();
        synchronized (this) {
            requestQueue.remove(Thread.currentThread());
            notifyAll();
        }
    }

    /**
     * Kończy proces pisania dla podanego pisarza.
     *
     * @param writer obiekt pisarza.
     */
    void stopWriting(Writer writer) {
        writersInLibrary = writersInLibrary.stream().filter(w -> !w.equals(writer)).collect(Collectors.toList());
        sendCommunicate(WRITER_CONST + writer.getWriterId() + " skończył pisać.");
        logWhoIsInLibrary();
        wrt.release();
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * Wypisuje obecnych w czytelni czytelników i pisarzy.
     */
    void logWhoIsInLibrary() {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("W czytelni jest ");
        messageBuilder.append(readersInLibrary.size());
        messageBuilder.append(" czytelników oraz ");
        messageBuilder.append(writersInLibrary.size());
        messageBuilder.append(" pisarzy: ");
        readersInLibrary.forEach(r -> messageBuilder.append(READER_CONST).append(r.getReaderId()).append(", "));
        writersInLibrary.forEach(w -> messageBuilder.append(WRITER_CONST).append(w.getWriterId()).append(", "));
        sendCommunicate(messageBuilder.toString());
    }

    /**
     * Wypisuje oczekujących w kolejce na dostęp do czytelni.
     */
    void logWhoIsInQueue() {
        StringBuilder messageBuilder = new StringBuilder();
        StringBuilder details = new StringBuilder();
        int readersInQueue = 0;
        int writersInQueue = 0;

        for (Thread thread : requestQueue) {
            if (thread instanceof Reader reader) {
                readersInQueue++;
                details.append(READER_CONST).append(reader.getReaderId()).append(", ");
            } else if (thread instanceof Writer writer) {
                writersInQueue++;
                details.append(WRITER_CONST).append(writer.getWriterId()).append(", ");
            }
        }
        messageBuilder.append("W kolejce jest ");
        messageBuilder.append(readersInQueue);
        messageBuilder.append(" czytelników oraz ");
        messageBuilder.append(writersInQueue);
        messageBuilder.append(" pisarzy: ");
        messageBuilder.append(details);
        sendCommunicate(messageBuilder.toString());
    }

    /**
     * Wysyła komunikat do konsoli.
     *
     * @param communicate tekst komunikatu.
     */
    void sendCommunicate(String communicate) {
        System.out.println(communicate);
    }
}
