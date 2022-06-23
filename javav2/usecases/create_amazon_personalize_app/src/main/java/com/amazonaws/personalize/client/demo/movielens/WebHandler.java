/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.demo.movielens;

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;

import com.amazonaws.personalize.client.demo.movielens.RecommendationsInterface.Item;
import com.amazonaws.personalize.client.demo.movielens.RecommendationsInterface.UserEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebHandler extends AbstractHandler {

    private final RecommendationsInterface recommender;

    public WebHandler(RecommendationsInterface recommender) {
        this.recommender = recommender;
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {

        System.out.println(target);
        try {
            if (target.startsWith("/recommendations/user")) {
                String userId = target.substring(target.lastIndexOf('/') + 1);
                System.out.println(userId);
                PrintWriter writer = response.getWriter();
                List<Item> list = recommender.getItemsForUser(userId);
                ObjectMapper mapper = new ObjectMapper();
                String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);

                writer.print(jsonResult);
                response.setStatus(200);
                writer.close();
            } else if (target.startsWith("/recommendations/item")) {
                String itemId = target.substring(target.lastIndexOf('/') + 1);
                System.out.println(itemId);
                PrintWriter writer = response.getWriter();
                List<Item> list = recommender.getItemsForItem(itemId);
                ObjectMapper mapper = new ObjectMapper();
                String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);

                writer.print(jsonResult);
                response.setStatus(200);
                writer.close();
            } else if (target.startsWith("/event")) {
                String data = IOUtils.toString(request.getInputStream());
                ObjectMapper om = new ObjectMapper();
                UserEvent e = om.readValue(data, UserEvent.class);
                recommender.putEvent(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {

        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(8080);
        server.addConnector(connector);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setWelcomeFiles(new String[]{"homepage.html"});
        resource_handler.setResourceBase("static/web/");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, new WebHandler(new DummyRecommender())});
        server.setHandler(handlers);

        server.start();

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("http://localhost:8080"));
        }
        server.join();
    }

    private static class DummyRecommender implements RecommendationsInterface {

        public List<Item> getItemsForUser(String userId) throws IOException {
            List<Item> list = new ArrayList<Item>();
            list.add(new Item("1", "one"));
            list.add(new Item("2", "two"));
            list.add(new Item("3", "three"));
            list.add(new Item("4", "four"));
            list.add(new Item("5", "five"));
            list.add(new Item("6", "six"));
            list.add(new Item("7", "seven"));
            list.add(new Item("8", "eight"));
            list.add(new Item("9", "nine"));
            list.add(new Item("10", "ten"));
            list.add(new Item("11", "eleven"));
            list.add(new Item("12", "twelve"));
            list.add(new Item("13", "thirteen"));
            list.add(new Item("14", "fourteen"));
            list.add(new Item("15", "fifteen"));
            list.add(new Item("16", "sixteen"));
            list.add(new Item("17", "seventeen"));
            list.add(new Item("18", "eighteen"));
            list.add(new Item("19", "nineteen"));
            list.add(new Item("20", "twenty"));
            list.add(new Item("21", "twentyone"));
            list.add(new Item("22", "twentytwo"));
            list.add(new Item("23", "twentythree"));
            list.add(new Item("24", "twentyfour"));
            list.add(new Item("25", "twentyfive"));
            return list;
        }

        public List<Item> getItemsForItem(String userId) throws IOException {
            List<Item> list = new ArrayList<Item>();
            list.add(new Item("1", "_one"));
            list.add(new Item("2", "_two"));
            list.add(new Item("3", "_three"));
            list.add(new Item("4", "_four"));
            list.add(new Item("5", "_five"));
            list.add(new Item("6", "_six"));
            list.add(new Item("7", "seven"));
            list.add(new Item("8", "eight"));
            list.add(new Item("9", "nine"));
            list.add(new Item("10", "ten"));
            list.add(new Item("11", "eleven"));
            list.add(new Item("12", "twelve"));
            list.add(new Item("13", "thirteen"));
            list.add(new Item("14", "fourteen"));
            list.add(new Item("15", "fifteen"));
            list.add(new Item("16", "sixteen"));
            list.add(new Item("17", "seventeen"));
            list.add(new Item("18", "eighteen"));
            list.add(new Item("19", "nineteen"));
            list.add(new Item("20", "twenty"));
            list.add(new Item("21", "twentyone"));
            list.add(new Item("22", "twentytwo"));
            list.add(new Item("23", "twentythree"));
            list.add(new Item("24", "twentyfour"));
            list.add(new Item("25", "twentyfive"));
            return list;
        }

        public void putEvent(UserEvent e) throws IOException {
            System.out.println("Event: " + e);
        }

    }


}
