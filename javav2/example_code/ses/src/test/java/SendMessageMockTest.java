import com.example.ses.SendMessage;
import com.example.ses.SendMessageAttachment;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import software.amazon.awssdk.services.ses.SesClient;
import javax.mail.MessagingException;
import java.io.IOException;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SendMessageMockTest {

    private static SesClient client;

    // This value is set as an input parameter
    private static String SENDER = "Please specify a sender email";

    // This value is set as an input parameter
    private static String RECIPIENT = "Please specify the RECIPIENT email";;

    // This value is set as an input parameter
    private static String SUBJECT = "UNIT Test";

    private static String FileLocation = "Please specify the full path to an Excel file to use as an attachment";


    // The email body for recipients with non-HTML email clients
    private static String BODY_TEXT = "Hello,\r\n" + "Here is a list of customers to contact.";

    // The HTML body of the email
    private static String BODY_HTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
            + "<p>Here is a list of customers to contact.</p>" + "</body>" + "</html>";


    @BeforeAll
    public static void setUp() {
        try {
        client = mock(SesClient.class);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSS3Service_thenNotNull() {
        assertThat(client).isNotNull();
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void sendtest() {

        try {
        SendMessage.send(client, SENDER,RECIPIENT, SUBJECT,BODY_TEXT,BODY_HTML );

    } catch (IOException | MessagingException e) {
        e.getStackTrace();
    }
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void sendTestAtt() {
        try {
            SendMessageAttachment.sendemailAttachment(client, SENDER, RECIPIENT, SUBJECT, BODY_TEXT, BODY_HTML,FileLocation);

        } catch (IOException | MessagingException e) {
            e.getStackTrace();
        }
        System.out.println("Test 3 passed");

    }
}
