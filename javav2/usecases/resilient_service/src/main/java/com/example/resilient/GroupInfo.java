/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.resilient;

public class GroupInfo {

    private String groupName;
    private boolean portOpen = false;

    public GroupInfo() {
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isPortOpen() {
        return portOpen;
    }

    public void setPortOpen(boolean portOpen) {
        this.portOpen = portOpen;
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
            "groupName='" + groupName + '\'' +
            ", postOpen=" + portOpen +
            '}';
    }
}
