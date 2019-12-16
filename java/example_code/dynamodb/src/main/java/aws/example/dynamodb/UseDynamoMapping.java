// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[UseDynamoMapping.java demonstrates how to use the DynamoDB mapping functionality]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-12-16]
// snippet-sourceauthor:[scmacdon-AWS]

/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

// snippet-start:[dynamodb.java.dynamoDB_mapping.complete]
package aws.example.dynamodb;

// snippet-start:[dynamodb.java.dynamoDB_mapping.import]
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
// snippet-end:[dynamodb.java.dynamoDB_mapping.import]

public class UseDynamoMapping {

    public static void main(String[] args)
    {
        // snippet-start:[dynamodb.java.dynamoDB_mapping.main]
       AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
       MusicItems items = new MusicItems();

       try{
          //add new content to the Music table
           items.setArtist("Famous Band");
           items.setSongTitle("Our Famous Song");
           items.setAlbumTitle("Our First Album");
           items.setAwards(0);

           // Save the item.
           DynamoDBMapper mapper = new DynamoDBMapper(client);
           mapper.save(items);

            //Load an item based on the Partition Key and Sort Key
           //both values need to be passed to the mapper.load method
           String artist = "Famous Band";
           String songQueryTitle = "Our Famous Song";

           // Retrieve the item.
           MusicItems itemRetrieved = mapper.load(MusicItems.class, artist, songQueryTitle);
           System.out.println("Item retrieved:");
           System.out.println(itemRetrieved);

            //Modify the Award value
           itemRetrieved.setAwards(2);
           mapper.save(itemRetrieved);
           System.out.println("Item updated:");
           System.out.println(itemRetrieved);

           System.out.print("Done");
       }
       catch (AmazonDynamoDBException e)
       {
           e.getStackTrace();
       }
    }

    @DynamoDBTable(tableName="Music")
    public static class MusicItems {

        //Set up Data Members that correspond to columns in the Music table
        private String artist;
        private String songTitle;
        private String albumTitle;
        private int awards;

        @DynamoDBHashKey(attributeName="Artist")
        public String getArtist() { return this.artist; }
        public void setArtist(String artist) {this.artist = artist; }

        @DynamoDBRangeKey(attributeName="SongTitle")
        public String getSongTitle() { return this.songTitle; }
        public void setSongTitle(String title) {this.songTitle = title; }

        @DynamoDBAttribute(attributeName="AlbumTitle")
        public String getAlbumTitle() { return this.albumTitle; }
        public void setAlbumTitle(String title) {this.albumTitle = title; }

        @DynamoDBAttribute(attributeName="Awards")
        public int getAwards() { return this.awards; }
        public void setAwards(int awards) {this.awards = awards; }
    }
    // snippet-end:[dynamodb.java.dynamoDB_mapping.main]
}
// snippet-end:[dynamodb.java.dynamoDB_mapping.complete]
