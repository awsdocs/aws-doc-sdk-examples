package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.photo.WorkItem;
import com.example.photo.services.DynamoDBService;

import java.util.List;

public class GetHandler implements RequestHandler<Object, List<WorkItem>> {
    @Override
    public List<WorkItem> handleRequest(Object o, Context context) {
        return new DynamoDBService().scanPhotoTable();
    }
}
