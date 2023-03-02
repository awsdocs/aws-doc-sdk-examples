package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.photo.services.S3Service;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

public class UploadHandler implements RequestHandler<Map<String, String>, String> {

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        LambdaLogger logger = context.getLogger();
        String fileName = event.get("filename");
        UUID uuid = UUID.randomUUID();
        String unqueFileName = uuid +"-"+fileName;

        logger.log("The file name is: " + fileName);
        S3Service s3Service = new S3Service();
        // create object
        MyData data = new MyData();

        String signedURL = s3Service.signObjectToUpload(unqueFileName);
        context.getLogger().log(signedURL);
        return signedURL;
        /*
        data.setURL(signedURL);

        // convert object to JSON
        Gson gson = new Gson();
        return gson.toJson(data);

         */
    }

    static class MyData {
        private String url;

        public String getURL() {
            return url;
        }

        public void setURL(String url) {
            this.url = url;
        }
    }
}
