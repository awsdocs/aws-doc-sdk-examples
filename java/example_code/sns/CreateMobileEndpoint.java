// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[sns.java.create_mobile_endpoint]
class RegistrationExample {

  AmazonSNSClient client = new AmazonSNSClient(); //provide credentials here
  String arnStorage = null;

  public void registerWithSNS() {

    String endpointArn = retrieveEndpointArn();
    String token = "Retrieved from the mobile operating system";

    boolean updateNeeded = false;
    boolean createNeeded = (null == endpointArn);

    if (createNeeded) {
      // No platform endpoint ARN is stored; need to call createEndpoint.
      endpointArn = createEndpoint();
      createNeeded = false;
    }

    System.out.println("Retrieving platform endpoint data...");
    // Look up the platform endpoint and make sure the data in it is current, even if
    // it was just created.
    try {
      GetEndpointAttributesRequest geaReq =
          new GetEndpointAttributesRequest()
        .withEndpointArn(endpointArn);
      GetEndpointAttributesResult geaRes =
        client.getEndpointAttributes(geaReq);

      updateNeeded = !geaRes.getAttributes().get("Token").equals(token)
        || !geaRes.getAttributes().get("Enabled").equalsIgnoreCase("true");

    } catch (NotFoundException nfe) {
      // We had a stored ARN, but the platform endpoint associated with it
      // disappeared. Recreate it.
        createNeeded = true;
    }

    if (createNeeded) {
      createEndpoint(token);
    }

    System.out.println("updateNeeded = " + updateNeeded);

    if (updateNeeded) {
      // The platform endpoint is out of sync with the current data;
      // update the token and enable it.
      System.out.println("Updating platform endpoint " + endpointArn);
      Map attribs = new HashMap();
      attribs.put("Token", token);
      attribs.put("Enabled", "true");
      SetEndpointAttributesRequest saeReq =
          new SetEndpointAttributesRequest()
        .withEndpointArn(endpointArn)
        .withAttributes(attribs);
      client.setEndpointAttributes(saeReq);
    }
  }

  /**
  * @return never null
  * */
  private String createEndpoint(String token) {

    String endpointArn = null;
    try {
      System.out.println("Creating platform endpoint with token " + token);
      CreatePlatformEndpointRequest cpeReq =
          new CreatePlatformEndpointRequest()
        .withPlatformApplicationArn(applicationArn)
        .withToken(token);
      CreatePlatformEndpointResult cpeRes = client
        .createPlatformEndpoint(cpeReq);
      endpointArn = cpeRes.getEndpointArn();
    } catch (InvalidParameterException ipe) {
      String message = ipe.getErrorMessage();
      System.out.println("Exception message: " + message);
      Pattern p = Pattern
        .compile(".*Endpoint (arn:aws:sns[^ ]+) already exists " +
                 "with the same [Tt]oken.*");
      Matcher m = p.matcher(message);
      if (m.matches()) {
        // The platform endpoint already exists for this token, but with additional
        // custom data that createEndpoint doesn't want to overwrite. Use the
        // existing platform endpoint.
        endpointArn = m.group(1);
      } else {
        // Rethrow the exception, because the input is actually bad.
        throw ipe;
      }
    }
    storeEndpointArn(endpointArn);
    return endpointArn;
  }

  /**
  * @return the ARN the app was registered under previously, or null if no
  *         platform endpoint ARN is stored.
  */
  private String retrieveEndpointArn() {
    // Retrieve the platform endpoint ARN from permanent storage,
    // or return null if null is stored.
    return arnStorage;
  }

  /**
  * Stores the platform endpoint ARN in permanent storage for lookup next time.
  * */
  private void storeEndpointArn(String endpointArn) {
    // Write the platform endpoint ARN to permanent storage.
    arnStorage = endpointArn;
  }
}
// snippet-end:[sns.java.create_mobile_endpoint]
