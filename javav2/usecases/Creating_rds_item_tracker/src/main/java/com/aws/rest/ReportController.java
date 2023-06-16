/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import com.google.gson.Gson;
import jxl.write.WriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/items:report")
public class ReportController {

    private final DatabaseService dbService;
    private final WriteExcel writeExcel;
    private final WriteExcel.SendMessages sm;

    @Autowired()
    ReportController(
        DatabaseService dbService,
        WriteExcel writeExcel,
        WriteExcel.SendMessages sm
    ) {
        this.dbService = dbService;
        this.writeExcel = writeExcel;
        this.sm = sm;
    }

    @PostMapping("")
    public String sendReport(@RequestBody Map<String, String> body) {
        List<WorkItem> list = dbService.getItemsDataSQLReport(0);
        try {
            InputStream is = writeExcel.write(list);
            sm.sendReport(is, body.get("email"));
            Gson gson = new Gson();
            return gson.toJson("ok");

        } catch (IOException | WriteException e) {
            e.printStackTrace();
        }
        return gson.toJson("error");
    }
}