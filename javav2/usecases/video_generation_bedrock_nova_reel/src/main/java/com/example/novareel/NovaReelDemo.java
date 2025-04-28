// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.novareel;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NovaReelDemo {

    public static void main(String[] args) {

        ConfigurableApplicationContext run = new SpringApplicationBuilder(NovaReelDemo.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

}
