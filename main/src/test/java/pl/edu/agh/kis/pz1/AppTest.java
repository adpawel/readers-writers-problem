package pl.edu.agh.kis.pz1;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {
    @Test
    void testMainWithValidArguments() {
        // Przechwycenie System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"3", "2"};

        App.main(args);

        String output = outContent.toString();

        assertTrue(output.contains("Pisarz 1 wystartował"));
        assertTrue(output.contains("Pisarz 2 wystartował"));
        assertTrue(output.contains("Czytelnik 1 wystartował"));
        assertTrue(output.contains("Czytelnik 3 wystartował"));
    }
}

