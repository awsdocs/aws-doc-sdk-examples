package com.example.s3.util;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.MutableLogEvent;

import java.util.LinkedHashMap;
import java.util.Map;

@Plugin(
        name = "MemoryLog4jAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class MemoryLog4jAppender extends AbstractAppender {

    private Map<String, String> eventMap = new LinkedHashMap<>();

    protected MemoryLog4jAppender(String name, Filter filter) {
        super(name, filter, null);
    }

    @PluginFactory
    public static MemoryLog4jAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter) {
        return new MemoryLog4jAppender(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        MutableLogEvent eventWithParameters = (MutableLogEvent) event;
        if (eventWithParameters.getParameterCount() == 2) {
            eventMap.put(eventWithParameters.getParameters()[0].toString(), eventWithParameters.getParameters()[1].toString());
        } else {
            eventMap.put (eventWithParameters.getFormattedMessage(), null);
        }
    }

    public Map<String, String> getEventMap(){
        return this.eventMap;
    }
}
