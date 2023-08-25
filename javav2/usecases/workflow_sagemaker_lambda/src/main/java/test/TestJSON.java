package test;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TestJSON {

    public static void main(String[] args) {
        String inputConfigJSON = "{\"DataSourceConfig\":{\"Type\":\"S3_DATA\",\"S3Data\":{\"KmsKeyId\":\"\",\"S3Uri\":\"s3:\\/\\/sagemaker-sdk-example-bucket\\/samplefiles\\/latlongtest.csv\"}},\"DocumentType\":{\"Value\":\"CSV\"}}";


        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(inputConfigJSON);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Get the DataSourceConfig object
        JSONObject dataSourceConfig = (JSONObject) jsonObject.get("DataSourceConfig");

        // Get the S3Data object
        JSONObject s3DataOb = (JSONObject) dataSourceConfig.get("S3Data");

        // Extract the S3URI
        String s3Uri = (String) s3DataOb.get("S3Uri");
        System.out.println("**** NEW S3URI: " + s3Uri);

    }
}