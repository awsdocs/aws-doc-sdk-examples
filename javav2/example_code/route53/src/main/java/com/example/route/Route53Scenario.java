// snippet-sourcedescription:[Route53Scenario.java demonstrates how to perform multiple operations using the AWS SDK for Java (v2).]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Route 53]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.route;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.model.Route53Exception;
import software.amazon.awssdk.services.route53domains.Route53DomainsClient;
import software.amazon.awssdk.services.route53domains.model.BillingRecord;
import software.amazon.awssdk.services.route53domains.model.CheckDomainAvailabilityRequest;
import software.amazon.awssdk.services.route53domains.model.CheckDomainAvailabilityResponse;
import software.amazon.awssdk.services.route53domains.model.CheckDomainTransferabilityRequest;
import software.amazon.awssdk.services.route53domains.model.CheckDomainTransferabilityResponse;
import software.amazon.awssdk.services.route53domains.model.ContactDetail;
import software.amazon.awssdk.services.route53domains.model.ContactType;
import software.amazon.awssdk.services.route53domains.model.CountryCode;
import software.amazon.awssdk.services.route53domains.model.DomainPrice;
import software.amazon.awssdk.services.route53domains.model.DomainSuggestion;
import software.amazon.awssdk.services.route53domains.model.DomainSummary;
import software.amazon.awssdk.services.route53domains.model.GetDomainDetailRequest;
import software.amazon.awssdk.services.route53domains.model.GetDomainDetailResponse;
import software.amazon.awssdk.services.route53domains.model.GetDomainSuggestionsRequest;
import software.amazon.awssdk.services.route53domains.model.GetDomainSuggestionsResponse;
import software.amazon.awssdk.services.route53domains.model.GetOperationDetailRequest;
import software.amazon.awssdk.services.route53domains.model.GetOperationDetailResponse;
import software.amazon.awssdk.services.route53domains.model.ListDomainsRequest;
import software.amazon.awssdk.services.route53domains.model.ListDomainsResponse;
import software.amazon.awssdk.services.route53domains.model.ListOperationsRequest;
import software.amazon.awssdk.services.route53domains.model.ListOperationsResponse;
import software.amazon.awssdk.services.route53domains.model.ListPricesRequest;
import software.amazon.awssdk.services.route53domains.model.ListPricesResponse;
import software.amazon.awssdk.services.route53domains.model.OperationSummary;
import software.amazon.awssdk.services.route53domains.model.RegisterDomainRequest;
import software.amazon.awssdk.services.route53domains.model.RegisterDomainResponse;
import software.amazon.awssdk.services.route53domains.model.ViewBillingRequest;
import software.amazon.awssdk.services.route53domains.model.ViewBillingResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

//snippet-start:[route.java2.scenario.main]
/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This Java code examples performs the following operations:
 *
 * 1. List current domains.
 * 2. List operations in the past year.
 * 3. View billing for the account in the past year.
 * 4. View prices for domain types.
 * 5. Get domain suggestions.
 * 6. Check domain availability.
 * 7. Check domain transferability.
 * 8. Request a domain registration.
 * 9. Get an operation detail
 * 10. Optionally, get a domain detail.
 */

public class Route53Scenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage:\n" +
            "    <domainType> <phoneNumber> <email> <domainSuggestion>\n\n" +
            "Where:\n" +
            "    domainType - The domain type (for example, com). \n" +
            "    phoneNumber - The phone number to use (for example, +91.9966564xxx)  "+
            "    email - The email address to use.  "+
            "    domainSuggestion - The domain suggestion (for example, findmy.accountants). \n" ;

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String domainType = args[0];
        String phoneNumber = args[1];
        String email = args[2] ;
        String domainSuggestion = args[3] ;
        Region region = Region.US_EAST_1;
        Route53DomainsClient route53DomainsClient = Route53DomainsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        System.out.println(DASHES);
        System.out.println("Welcome to the Amazon Route 53 domains example scenario.");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("1. List current domains.");
        listDomains(route53DomainsClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. List operations in the past year.");
        listOperations(route53DomainsClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. View billing for the account in the past year.");
        listBillingRecords(route53DomainsClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. View prices for domain types.");
        listPrices(route53DomainsClient, domainType);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Get domain suggestions.");
        listDomainSuggestions(route53DomainsClient, domainSuggestion);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. Check domain availability.");
        checkDomainAvailability(route53DomainsClient, domainSuggestion);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Check domain transferability.");
        checkDomainTransferability(route53DomainsClient, domainSuggestion);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("8. Request a domain registration.");
        String opId = requestDomainRegistration(route53DomainsClient, domainSuggestion, phoneNumber, email);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("9. Get an operation detail.");
        getOperationalDetail(route53DomainsClient, opId);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("10. Get domain details.");
        System.out.println("Note: you must have a registered domain to get details.");
        System.out.println("Otherwise an exception is thrown that states " );
        System.out.println("Domain xxxxxxx not found in xxxxxxx account.");
        getDomainDetails(route53DomainsClient, domainSuggestion);
        System.out.println(DASHES);

    }

    //snippet-start:[route.java2.domaindetails.main]
    public static void getDomainDetails(Route53DomainsClient route53DomainsClient, String domainSuggestion){
        try {
            GetDomainDetailRequest detailRequest = GetDomainDetailRequest.builder()
                .domainName(domainSuggestion)
                .build();

            GetDomainDetailResponse response = route53DomainsClient.getDomainDetail(detailRequest);
            System.out.println("The contact first name is " + response.registrantContact().firstName());
            System.out.println("The contact last name is " + response.registrantContact().lastName());
            System.out.println("The contact org name is " + response.registrantContact().organizationName());

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[route.java2.domaindetails.main]

    //snippet-start:[route.java2.domainoperations.main]
    public static void getOperationalDetail(Route53DomainsClient route53DomainsClient, String operationId) {
        try {
            GetOperationDetailRequest detailRequest = GetOperationDetailRequest.builder()
                .operationId(operationId)
                .build();

            GetOperationDetailResponse response = route53DomainsClient.getOperationDetail(detailRequest);
            System.out.println("Operation detail message is "+response.message());

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[route.java2.domainoperations.main]

    //snippet-start:[route.java2.domainreg.main]
    public static String requestDomainRegistration(Route53DomainsClient route53DomainsClient,
                                                   String domainSuggestion,
                                                   String phoneNumber,
                                                   String email) {

        try {
            ContactDetail contactDetail = ContactDetail.builder()
                .contactType(ContactType.COMPANY)
                .state("LA")
                .countryCode(CountryCode.IN)
                .email(email)
                .firstName("Scott")
                .lastName("Macdonald")
                .city("Delhi")
                .phoneNumber(phoneNumber)
                .organizationName("My Org")
                .addressLine1("Carp")
                .zipCode("123 123")
                .build();

            RegisterDomainRequest domainRequest = RegisterDomainRequest.builder()
                .adminContact(contactDetail)
                .registrantContact(contactDetail)
                .techContact(contactDetail)
                .domainName(domainSuggestion)
                .autoRenew(true)
                .durationInYears(1)
                .build();

            RegisterDomainResponse response = route53DomainsClient.registerDomain(domainRequest);
            System.out.println("Registration requested. Operation Id: " +response.operationId());
            return response.operationId();

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    //snippet-end:[route.java2.domainreg.main]

    //snippet-start:[route.java2.checkdomaintransfer.main]
    public static void checkDomainTransferability(Route53DomainsClient route53DomainsClient, String domainSuggestion){
        try {
            CheckDomainTransferabilityRequest transferabilityRequest = CheckDomainTransferabilityRequest.builder()
                .domainName(domainSuggestion)
                .build();

            CheckDomainTransferabilityResponse response = route53DomainsClient.checkDomainTransferability(transferabilityRequest);
            System.out.println("Transferability: "+response.transferability().transferable().toString());


        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[route.java2.checkdomaintransfer.main]

    //snippet-start:[route.java2.checkdomainavailability.main]
    public static void checkDomainAvailability(Route53DomainsClient route53DomainsClient, String domainSuggestion) {
        try {
            CheckDomainAvailabilityRequest availabilityRequest = CheckDomainAvailabilityRequest.builder()
                .domainName(domainSuggestion)
                .build();

            CheckDomainAvailabilityResponse response = route53DomainsClient.checkDomainAvailability(availabilityRequest);
            System.out.println(domainSuggestion +" is "+response.availability().toString());

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[route.java2.checkdomainavailability.main]

    //snippet-start:[route.java2.domainsuggestions.main]
    public static void listDomainSuggestions(Route53DomainsClient route53DomainsClient, String domainSuggestion) {
        try {
            GetDomainSuggestionsRequest suggestionsRequest = GetDomainSuggestionsRequest.builder()
                .domainName(domainSuggestion)
                .suggestionCount(5)
                .onlyAvailable(true)
                .build();

            GetDomainSuggestionsResponse response = route53DomainsClient.getDomainSuggestions(suggestionsRequest);
            List<DomainSuggestion> suggestions = response.suggestionsList();
            for (DomainSuggestion suggestion: suggestions) {
                System.out.println("Suggestion Name: "+suggestion.domainName());
                System.out.println("Availability: "+suggestion.availability());
                System.out.println(" ");
            }

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[route.java2.domainsuggestions.main]

    //snippet-start:[route.java2.domainprices.main]
    public static void listPrices(Route53DomainsClient route53DomainsClient, String domainType) {
        try {
            ListPricesRequest pricesRequest = ListPricesRequest.builder()
                .maxItems(10)
                .tld(domainType)
                .build();

            ListPricesResponse response = route53DomainsClient.listPrices(pricesRequest);
            List<DomainPrice> prices = response.prices();
            for (DomainPrice pr: prices) {
                System.out.println("Name: "+pr.name());
                System.out.println("Registration: "+pr.registrationPrice().price() + " " +pr.registrationPrice().currency());
                System.out.println("Renewal: "+pr.renewalPrice().price() + " " +pr.renewalPrice().currency());
                System.out.println("Transfer: "+pr.transferPrice().price() + " " +pr.transferPrice().currency());
                System.out.println("Transfer: "+pr.transferPrice().price() + " " +pr.transferPrice().currency());
                System.out.println("Change Ownership: "+pr.changeOwnershipPrice().price() + " " +pr.changeOwnershipPrice().currency());
                System.out.println("Restoration: "+pr.restorationPrice().price() + " " +pr.restorationPrice().currency());
                System.out.println(" ");
            }

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[route.java2.domainprices.main]

    //snippet-start:[route.java2.domainbillingrecords.main]
    public static void listBillingRecords(Route53DomainsClient route53DomainsClient) {
        try {
            Date currentDate = new Date();
            LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            ZoneOffset zoneOffset = ZoneOffset.of("+05:30");
            LocalDateTime localDateTime2 = localDateTime.minusYears(1);
            Instant myStartTime = localDateTime2.toInstant(zoneOffset);
            Instant myEndTime = localDateTime.toInstant(zoneOffset);

            ViewBillingRequest viewBillingRequest = ViewBillingRequest.builder()
                .maxItems(10)
                .start(myStartTime)
                .end(myEndTime)
                .build();

            ViewBillingResponse response = route53DomainsClient.viewBilling(viewBillingRequest);
            List<BillingRecord> records = response.billingRecords();
            if (records.isEmpty()) {
                System.out.println("\tNo billing records found for the past year.");
            } else {
                for (BillingRecord record : records) {
                    System.out.println("Bill Date: "+record.billDate());
                    System.out.println("Operation: "+ record.operationAsString());
                    System.out.println("Price: "+record.price());
                }
            }

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[route.java2.domainbillingrecords.main]

    //snippet-start:[route.java2.domainlistops.main]
    public static void listOperations(Route53DomainsClient route53DomainsClient) {
        try {
            Date currentDate = new Date();
            LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            ZoneOffset zoneOffset = ZoneOffset.of("+05:30");
            localDateTime = localDateTime.minusYears(1);
            Instant myTime = localDateTime.toInstant(zoneOffset);

            ListOperationsRequest operationsRequest = ListOperationsRequest.builder()
                .submittedSince(myTime)
                .maxItems(10)
                .build();

            ListOperationsResponse response = route53DomainsClient.listOperations(operationsRequest);
            List<OperationSummary> operations = response.operations();
            if (operations.isEmpty()) {
                System.out.println("\tNo operations found for the past year.");
            } else {
                for (OperationSummary op : operations) {
                    System.out.println("Operation Id: "+op.operationId());
                    System.out.println("\tStatus: "+ op.statusAsString());
                    System.out.println("\tDate: "+op.submittedDate());
                }
            }

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //end-end:[route.java2.domainlistops.main]

    //snippet-start:[route.java2.domainlist.main]
    public static void listDomains(Route53DomainsClient route53DomainsClient) {
        try {
            ListDomainsRequest domainsRequest = ListDomainsRequest.builder()
                .maxItems(10)
                .build();

            ListDomainsResponse response = route53DomainsClient.listDomains(domainsRequest);
            List<DomainSummary> domains = response.domains();
            if (domains.isEmpty()) {
                System.out.println("No domains found in this account.");
            } else {
                for (DomainSummary summary : domains) {
                    System.out.println("The domain name is "+summary.domainName()) ;
                }
            }

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[route.java2.domainlist.main]
}
//snippet-end:[route.java2.scenario.main]