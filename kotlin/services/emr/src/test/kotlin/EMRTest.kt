import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.emr.createAppCluster
import com.kotlin.emr.createFleet
import com.kotlin.emr.createSparkCluster
import com.kotlin.emr.describeMyCluster
import com.kotlin.emr.listAllClusters
import com.kotlin.emr.terminateFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.IOException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class EMRTest {
    private var jar = ""
    private var myClass = ""
    private var keys = ""
    private var logUri = ""
    private var name = ""
    private var jobFlowId = ""
    private var existingClusterId = ""

    @BeforeAll
    @Throws(IOException::class)
    fun setUp() = runBlocking {
        // Get the values to run these tests from AWS Secrets Manager.
        val gson = Gson()
        val json: String = getSecretValues()
        val values = gson.fromJson(json, SecretValues::class.java)
        jar = values.jar.toString()
        myClass = values.myClass.toString()
        keys = values.keys.toString()
        logUri = values.logUri.toString()
        name = values.name.toString()
        existingClusterId = values.existingClusterId.toString()

        /*
        try {
            EMRTest::class.java.classLoader.getResourceAsStream("config.properties").use { input ->
                val prop = Properties()
                if (input == null) {
                    println("Sorry, unable to find config.properties")
                    return
                }

                // load a properties file from class path, inside static method
                prop.load(input)

                // Populate the data members required for all tests
                jar = prop.getProperty("jar")
                myClass = prop.getProperty("myClass")
                keys = prop.getProperty("keys")
                logUri = prop.getProperty("logUri")
                name = prop.getProperty("name")
                existingClusterId = prop.getProperty("existingClusterId")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        */
    }

    @Test
    @Order(1)
    fun listClustersTest() = runBlocking {
        listAllClusters()
        println("Test 3 passed")
    }

     private suspend fun getSecretValues(): String {
        val secretName = "text/emr"
        val valueRequest = GetSecretValueRequest {
            secretId = secretName
        }
        SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/emr (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val existingClusterId: String? = null
        val jar: String? = null
        val myClass: String? = null
        val keys: String? = null
        val name: String? = null
        val logUri: String? = null
    }
}
