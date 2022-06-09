/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.aws.rest.RetrieveItems;
import com.aws.rest.SendMessage;
import com.aws.rest.WorkItem;
import com.aws.rest.WriteExcel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpringTests {

    private static String id="";
    private static String email="";

    @BeforeAll
    public static void setUp() {

        try (InputStream input = SpringTests.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            id = prop.getProperty("id");
            email = prop.getProperty("email");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void checkValues() {
        assertNotNull(id);
        assertNotNull(email);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void invokeArchive() {
        RetrieveItems ri = new RetrieveItems();
        List<WorkItem> myList = ri.getItemsDataSQLReport(1);
        assertNotNull(myList);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(3)
    public void invokeActive() {
        RetrieveItems ri = new RetrieveItems();
        List<WorkItem> myList = ri.getItemsDataSQLReport(1);
        assertNotNull(myList);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(4)
    public void flipItem(){
        RetrieveItems ri = new RetrieveItems();
        ri.flipItemArchive(id);
    }

    @Test
    @Order(5)
    public void sendReport() {
        SendMessage sm = new SendMessage();
        RetrieveItems ri = new RetrieveItems();
        WriteExcel writeExcel = new WriteExcel();
        List<WorkItem> theList = ri.getItemsDataSQLReport(0);
        java.io.InputStream is = writeExcel.exportExcel(theList);

        try {
            sm.sendReport(is, email);

        }catch (IOException e) {
            e.getStackTrace();
        }
    }
}
