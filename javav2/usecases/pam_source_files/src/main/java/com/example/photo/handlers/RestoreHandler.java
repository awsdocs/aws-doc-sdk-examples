/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.photo.endpoints.DownloadEndpoint;
import com.example.photo.services.DynamoDBService;
import com.example.photo.services.S3Service;
import com.example.photo.services.SnsService;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.photo.PhotoApplicationResources.toJson;

public class RestoreHandler implements RequestHandler<Map<String, Object>, String> {
    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        try {
            context.getLogger().log("RestoreHandler handleRequest" + toJson(input));
            JSONObject body = new JSONObject(input);
            List<String> labels = body.getJSONArray("labels")
                .toList()
                .stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .collect(Collectors.toList());
            context.getLogger().log("Restoring labels " + toJson(labels));

            DownloadEndpoint downloadEndpoint = new DownloadEndpoint(new DynamoDBService(), new S3Service(), new SnsService());
            String url = downloadEndpoint.download(labels);
            context.getLogger().log("Labels archived to URL " + url);

        } catch (JSONException e) {
            context.getLogger().log(e.getMessage());
        }
        return "";
    }
}
