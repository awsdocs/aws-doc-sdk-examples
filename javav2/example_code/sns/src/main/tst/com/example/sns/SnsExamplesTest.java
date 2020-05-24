package com.example.sns;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SnsExamplesTest {

    /**
     * The entry point, which results in calls to all test methods.
     *
     * @param args Command line arguments (ignored).
     */
    public static void main(String[] args) {
        SnsExamplesTest tester = new SnsExamplesTest();
        tester.runAllTests();
    }

    @Test
    public void runAllTests() {

    }

    @BeforeEach
    private void setup() {

    }


    @Test
    public void CheckOptOut_returnsSuccessful() {
        //GIVEN
        CheckOptOut checkOptOut = new CheckOptOut();
        String phoneNumber = "+12532561647";
        String[] args = new String[]{phoneNumber};
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));

        //WHEN - compute the average
        checkOptOut.main(args);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = buffer.toString();
        buffer.reset();

        //THEN - throw Illegal Argument Exception
        assertTrue(output.length() > 0 , "CheckOptOut should print a response");


    }


}