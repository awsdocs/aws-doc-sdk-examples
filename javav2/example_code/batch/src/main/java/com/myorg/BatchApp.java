package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

public class BatchApp {
    public static void main(final String[] args) {
        App app = new App();
        new BatchStack(app, "BatchStack4", StackProps.builder().build());

        // Instantiate the second stack (EcsStack)
        new EcsStack(app, "EcsStack", StackProps.builder().build());
        new RolesStack(app, "RolesStack", StackProps.builder().build());
        app.synth();
    }
}

