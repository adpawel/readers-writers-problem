Readers and Writers Problem

1. Opis
Projekt jest implementacją klasycznego problemu synchronizacji procesów — czytelników i pisarzy. Problem dotyczy współbieżnego dostępu dwóch rodzajów procesów do wspólnego zasobu:
- Czytelnicy mogą jednocześnie odczytywać dane, pod warunkiem że żaden pisarz nie dokonuje w tym czasie zmian.
- Pisarze mogą zmieniać dane, pod warunkiem że żadni czytelnicy ani inni pisarze nie mają do nich dostępu.

2. Rozwiązanie:
    a) Synchronizacja: Do synchronizacji wątków wykorzystano semafory (java.util.concurrent.Semaphore) oraz bloki synchronized{}:
        Czytelnicy są blokowani, jeśli osiągnięto maksymalny limit jednoczesnych czytelników w czytelni.
        Operacje na kolejce FIFO są chronione przez bloki synchronized{}, aby zachować ich poprawność w środowisku wielowątkowym.

    b) Zagłodzenie: Problem zagłodzenia został rozwiązany poprzez implementację kolejki FIFO (First In First Out), zapewniającej sprawiedliwy dostęp zarówno dla czytelników, jak i pisarzy.

    c) Czas dostępu:
        Czytelnik/pisarz przebywa w czytelni przez losowy czas od 1 do 3 sekund.
        Następnie czytelnik/pisarz czeka przez losowy czas, zanim ponownie spróbuje uzyskać dostęp.

3. Funkcjonalności:
- Limit jednoczesnych czytelników (domyślnie 5).
- Brak możliwości jednoczesnego odczytu i zapisu.
- Ochrona przed zagłodzeniem dla obu grup procesów.
- Kolejkowanie żądań dostępu z wykorzystaniem algorytmu FIFO.

4. Sposób Uruchomienia
    a) Wymagania:
    - Zainstalowane Java 17 (lub nowsza).
    - Plik JAR aplikacji: main-1.0-jar-with-dependencies.jar
    b) polecenie w terminalu:
        java -jar main-1.0-jar-with-dependencies.jar <liczba_czytelników> <liczba_pisarzy>