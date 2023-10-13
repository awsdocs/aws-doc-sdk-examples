//snippet-sourcedescription:[SearchImageSets.java demonstrates how to search for image sets in a data store.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.search_imagesets.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.*;
import software.amazon.awssdk.services.medicalimaging.paginators.SearchImageSetsIterable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//snippet-end:[medicalimaging.java2.search_imagesets.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class SearchImageSets {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <datastoreId> <patientId>\n\n" +
                "Where:\n" +
                "    datastoreId - The ID of the data store.\n" +
                "    patientId - The ID of the patient to search for.\\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String datastoreId = args[0];
        String patientId = args[1];

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        // Use case #1: EQUAL operator.
        //snippet-start:[medicalimaging.java2.search_imagesets.use_case1]
        List<SearchFilter> searchFilters = Collections.singletonList(SearchFilter.builder()
                .operator(Operator.EQUAL)
                .values(SearchByAttributeValue.builder()
                        .dicomPatientId(patientId)
                        .build())
                .build());

        List<ImageSetsMetadataSummary> imageSetsMetadataSummaries = searchMedicalImagingImageSets(medicalImagingClient,
                datastoreId, searchFilters);
        if (imageSetsMetadataSummaries != null) {
            System.out.println("The image sets for patient " + patientId + " are:\n" + imageSetsMetadataSummaries);
            System.out.println();
        }
        //snippet-end:[medicalimaging.java2.search_imagesets.use_case1]

        // Use case #2: BETWEEN operator using DICOMStudyDate and DICOMStudyTime.
        //snippet-start:[medicalimaging.java2.search_imagesets.use_case2]
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        searchFilters = Collections.singletonList(SearchFilter.builder()
                .operator(Operator.BETWEEN)
                .values(SearchByAttributeValue.builder()
                                .dicomStudyDateAndTime(DICOMStudyDateAndTime.builder()
                                        .dicomStudyDate("19990101")
                                        .dicomStudyTime("000000.000")
                                        .build())
                                .build(),
                        SearchByAttributeValue.builder()
                                .dicomStudyDateAndTime(DICOMStudyDateAndTime.builder()
                                        .dicomStudyDate((LocalDate.now().format(formatter)))
                                        .dicomStudyTime("000000.000")
                                        .build())
                                .build())
                .build());

        imageSetsMetadataSummaries = searchMedicalImagingImageSets(medicalImagingClient,
                datastoreId, searchFilters);
        if (imageSetsMetadataSummaries != null) {
            System.out.println("The image sets searched with BETWEEN operator using DICOMStudyDate and DICOMStudyTime are:\n" +
                    imageSetsMetadataSummaries);
            System.out.println();
        }
        //snippet-end:[medicalimaging.java2.search_imagesets.use_case2]

        // Use case #3: BETWEEN operator using createdAt. Time studies were previously persisted.
        //snippet-start:[medicalimaging.java2.search_imagesets.use_case3]
        searchFilters = Collections.singletonList(SearchFilter.builder()
                .operator(Operator.BETWEEN)
                .values(SearchByAttributeValue.builder()
                                .createdAt(Instant.parse("1985-04-12T23:20:50.52Z"))
                                .build(),
                        SearchByAttributeValue.builder()
                                .createdAt(Instant.now())
                                .build())
                .build());

        imageSetsMetadataSummaries = searchMedicalImagingImageSets(medicalImagingClient,
                datastoreId, searchFilters);
        if (imageSetsMetadataSummaries != null) {
            System.out.println("The image sets searched with BETWEEN operator using createdAt are:\n " + imageSetsMetadataSummaries);
            System.out.println();
        }
        //snippet-end:[medicalimaging.java2.search_imagesets.use_case3]

        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.search_imagesets.main]
    public static List<ImageSetsMetadataSummary> searchMedicalImagingImageSets(MedicalImagingClient medicalImagingClient,
                                                                               String datastoreId, List<SearchFilter> searchFilters) {
        try {
            SearchImageSetsRequest datastoreRequest = SearchImageSetsRequest.builder()
                    .datastoreId(datastoreId)
                    .searchCriteria(SearchCriteria.builder().filters(searchFilters).build())
                    .build();
            SearchImageSetsIterable responses = medicalImagingClient.searchImageSetsPaginator(datastoreRequest);
            List<ImageSetsMetadataSummary> imageSetsMetadataSummaries = new ArrayList<>();

            responses.stream().forEach(response -> imageSetsMetadataSummaries.addAll(response.imageSetsMetadataSummaries()));

            return imageSetsMetadataSummaries;
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
    }
    //snippet-end:[medicalimaging.java2.search_imagesets.main]
}
