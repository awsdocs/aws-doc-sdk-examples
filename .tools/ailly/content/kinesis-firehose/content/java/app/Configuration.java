package com.example.firehoseingestion.config;

import java.util.Properties;

public class Configuration {

    private static final String DELIVERY_STREAM_NAME_PROP = "delivery.stream.name";
    private static final String REGION_PROP = "region";

    private final Properties properties;

    public Configuration() {
        this.properties = loadProperties();
    }

    private Properties loadProperties() {
        // Load properties from environment variables or configuration files
        return System.getProperties();
    }

    public String getDeliveryStreamName() {
        return properties.getProperty(DELIVERY_STREAM_NAME_PROP);
    }

    public String getRegion() {
        return properties.getProperty(REGION_PROP);
    }
}