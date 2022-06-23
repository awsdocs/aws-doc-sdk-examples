/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.demo.movielens;

import java.io.IOException;
import java.util.List;

public interface RecommendationsInterface {

    class Item {
        final String item;
        final String title;

        public Item(String item, String title) {
            super();
            this.item = item;
            this.title = title;
        }

        public String getItem() {
            return item;
        }

        public String getTitle() {
            return title;
        }
    }

    class UserEvent {
        String userId;
        String itemId;
        String event;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        @Override
        public String toString() {
            return "UserEvent [userId=" + userId + ", itemId=" + itemId + ", event=" + event + "]";
        }
    }

    List<Item> getItemsForUser(String userId) throws IOException;

    List<Item> getItemsForItem(String userId) throws IOException;

    void putEvent(UserEvent e) throws IOException;

}
