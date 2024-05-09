// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.libs.demo;

import com.example.bedrockruntime.libs.demo.scenarios.Scenario;
import org.json.JSONArray;

import java.io.IOException;
import java.util.List;

import static com.example.bedrockruntime.libs.demo.Utils.*;

public class DemoRunner {
    public static final String PRESS_ENTER_TO_START_SCENARIO = "Press Enter to start the scenario...";
    public static final String PRESS_ENTER_TO_START_SCENARIO_WITH_ORDINAL = "Press Enter to start the %s scenario...";

    private final List<Scenario> scenarios;
    private final String promptTemplate;
    private final String titleTemplate;

    private final DemoState state = new DemoState();

    public DemoRunner(List<Scenario> scenarios) {
        this.scenarios = scenarios;
        this.titleTemplate = scenarios.size() != 1 ? "Scenario %s: %s" : "Scenario: %2$s";
        this.promptTemplate = scenarios.size() != 1 ? PRESS_ENTER_TO_START_SCENARIO_WITH_ORDINAL : PRESS_ENTER_TO_START_SCENARIO;
    }

    public void run() throws IOException {
        printHeader(scenarios);

        for (Scenario scenario : scenarios) {
            int number = scenarios.indexOf(scenario) + 1;
            String ordinal = getOrdinalFor(number);
            promptUser(promptTemplate.formatted(ordinal));
            printScenarioHeader(titleTemplate.formatted(number, scenario.getTitle()));
            scenario.run(state);
        }

        printFooter();
    }

    public static class DemoState {
        public JSONArray messages = new JSONArray();
    }
}
