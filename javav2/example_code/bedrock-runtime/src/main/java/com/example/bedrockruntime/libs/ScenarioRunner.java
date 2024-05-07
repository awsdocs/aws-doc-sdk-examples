// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.libs;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ScenarioRunner {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final List<String> scenarios = new ArrayList<>();

    public void printHeader() {
        System.out.println("*".repeat(80));
        System.out.println("*" + " ".repeat(78) + "*");
        System.out.println("*  Welcome to the Amazon Bedrock demo!" + " ".repeat(41) + "*");
        System.out.println("*  " + "-".repeat(74) + "  *");
        System.out.println("*" + " ".repeat(78) + "*");
        System.out.println("*  This demo showcases the following scenarios:" + " ".repeat(32) + "*");

        int scenarioNumber = 1;
        scenarios.forEach(scenario -> {
            System.out.println("*" + " ".repeat(78) + "*");
            System.out.println("*  " + scenarioNumber + ". " + scenario + "." + " ".repeat(20) + "*");
        });

        System.out.println("*" + " ".repeat(78) + "*");
        System.out.println("*".repeat(80));
    }

    public void printScenarioHeader(String title) {
        System.out.println("=".repeat(80));
        System.out.println(title);
        System.out.println("=".repeat(80));
    }

    public void printCurrentResponse(JSONObject response) throws IOException {
        printCurrentResponse(response.toString(2));
    }

    public void printCurrentResponse(String text) throws IOException {
        System.out.println("-".repeat(80));
        promptUser("Press Enter to see the detailed response...");
        System.out.println(text);
        System.out.println("-".repeat(80));
    }

    public void printFooter() {
        System.out.println("\n" + "*".repeat(80));
        System.out.println("Thanks for running the Amazon Bedrock demo!");
        System.out.println("=".repeat(80));
        System.out.println("For more examples across different programming languages check out:");
        System.out.println("https://docs.aws.amazon.com/bedrock/latest/userguide/service_code_examples.html");
        System.out.println("*".repeat(80));
    }

    public void promptUser(String text) throws IOException {
        System.out.printf("\n%s%n", text);
        this.reader.readLine();
    }

    public ScenarioRunner addScenario(String title) {
        this.scenarios.add(title);
        return this;
    }
}
