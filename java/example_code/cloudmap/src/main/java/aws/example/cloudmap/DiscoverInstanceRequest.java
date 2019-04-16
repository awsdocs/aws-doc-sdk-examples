/*
 * Author : Rajib Deb
 */
package aws.example.cloudmap;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.servicediscovery.AWSServiceDiscovery;
import com.amazonaws.services.servicediscovery.AWSServiceDiscoveryClientBuilder;
import com.amazonaws.services.servicediscovery.model.ListInstancesRequest;

public class DiscoverInstanceRequest {
	public static void main(String args[]) throws Exception
	{
		AWSCredentials credentials = null;
		try
		{
			credentials = new ProfileCredentialsProvider().getCredentials();
		}catch(Exception e)
		{
			throw new AmazonClientException("Cannot Load credentials");
		}
		
		AWSServiceDiscovery client = AWSServiceDiscoveryClientBuilder
									.standard()
									.withCredentials(new AWSStaticCredentialsProvider(credentials))
									.withRegion("us-east-1")
									.build();
		
		ListInstancesRequest lreq = new ListInstancesRequest();
		lreq.setServiceId("srv-l7gkxmjapm5givba");  //Replace with service id
		
		System.out.println(client.listInstances(lreq));
	}

}
