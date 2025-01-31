// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.libs.demo;

import com.example.bedrockruntime.libs.demo.scenarios.Scenario;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.util.ULocale;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static void printHeader(List<Scenario> scenarios) {
        System.out.println("*" .repeat(80));
        System.out.println("*" + " " .repeat(78) + "*");
        System.out.println("*  Welcome to the Amazon Bedrock demo!" + " " .repeat(41) + "*");
        System.out.println("*  " + "-" .repeat(74) + "  *");
        System.out.println("*" + " " .repeat(78) + "*");
        System.out.println("*  This demo showcases the following scenarios:" + " " .repeat(32) + "*");
        System.out.println("*" + " " .repeat(78) + "*");

        scenarios.forEach(scenario -> {
            int number = scenarios.indexOf(scenario) + 1;
            int blanks = 72 - scenario.getTitle().length() - String.valueOf(number).length();
            System.out.printf("*  %d. %s.%s *%n", number, scenario.getTitle(), " " .repeat(blanks));
        });

        System.out.println("*" + " " .repeat(78) + "*");
        System.out.println("*" .repeat(80));
    }

    public static void printScenarioHeader(String title) {
        System.out.println("=" .repeat(80));
        System.out.println(title);
        System.out.println("=" .repeat(80));
    }

    public static void printResponse(JSONObject response) throws IOException {
        System.out.println("-" .repeat(80));
        promptUser("Press Enter to see the detailed response...");

        System.out.println(response.toString(2));
        System.out.println("-" .repeat(80));
    }

    public static void printFooter() {
        System.out.println("\n" + "*" .repeat(80));
        System.out.println("Thanks for running the Amazon Bedrock demo!");
        System.out.println("=" .repeat(80));
        System.out.println("For more examples across different programming languages check out:");
        System.out.println("https://docs.aws.amazon.com/bedrock/latest/userguide/service_code_examples.html");
        System.out.println("*" .repeat(80));
    }

    public static void promptUser(String text) throws IOException {
        System.out.printf("%n%s%n", text);
        new BufferedReader(new InputStreamReader(System.in)).readLine();
    }

    public static String getOrdinalFor(int number) {
        var nf = new RuleBasedNumberFormat(ULocale.forLocale(Locale.ENGLISH), RuleBasedNumberFormat.ORDINAL);
        return nf.format(number);
    }
}
