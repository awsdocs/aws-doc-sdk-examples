package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;

// Handler value: example.Handler
public class Handler implements RequestHandler<Map<String,String>, String>{

    @Override
    public String handleRequest(Map<String,String> event, Context context)
    {
        LambdaLogger logger = context.getLogger();
        Gson gson = new GsonBuilder().create();

        // log execution details
        logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
        logger.log("CONTEXT: " + gson.toJson(context));
        // process event
        logger.log("EVENT Data: " + gson.toJson(event));

        String myCaseID = event.get("inputCaseID");
        logger.log("CaseId44: " + myCaseID);
        return myCaseID;
    }
}
