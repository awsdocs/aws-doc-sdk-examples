/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.etl.example;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Population {

    public String id;
    public String code;
    public String pop2010;
    public String pop2011;
    public String pop2012;
    public String pop2013;
    public String pop2014;
    public String pop2015;
    public String pop2016;
    public String pop2017;
    public String pop2018;
    public String pop2019;


    public void setId(String name) {
        this.id = name;
    }

    @DynamoDbPartitionKey
    public String getId() {
        return this.id ;
    }


    public void set2019(String num) {
        this.pop2019 = num;
    }

    public String get2019() {
        return this.pop2019;
    }

    public void set2018(String num) {
        this.pop2018 = num;
    }

    public String get2018() {
        return this.pop2018;
    }


    public void set2017(String num) {
        this.pop2017 = num;
    }

    public String get2017() {
        return this.pop2017;
    }


    public void set2016(String num) {
        this.pop2016 = num;
    }

    public String get2016() {
        return this.pop2016;
    }

    public void set2015(String num) {
        this.pop2015 = num;
    }

    public String get2015() {
        return this.pop2015;
    }


    public void set2014(String num) {
        this.pop2014 = num;
    }

    public String get2014() {
        return this.pop2014;
    }


    public void set2013(String num) {
        this.pop2013 = num;
    }

    public String get2013() {
        return this.pop2013;
    }


    public void set2012(String num) {
        this.pop2012 = num;
    }

    public String get2012() {
        return this.pop2012;
    }

    public void set2011(String num) {
        this.pop2011 = num;
    }

    public String get2011() {
        return this.pop2011;
    }


    public void set2010(String num) {
        this.pop2010 = num;
    }

    public String get2010() {
        return this.pop2010;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
