/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.projects.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.CreateProjectRequest;
import software.amazon.awssdk.services.lookoutvision.model.CreateProjectResponse;
import software.amazon.awssdk.services.lookoutvision.model.ProjectMetadata;
import software.amazon.awssdk.services.lookoutvision.paginators.ListProjectsIterable;
import software.amazon.awssdk.services.lookoutvision.model.ListProjectsRequest;
import software.amazon.awssdk.services.lookoutvision.model.DeleteProjectRequest;
import software.amazon.awssdk.services.lookoutvision.model.DeleteProjectResponse;
import software.amazon.awssdk.services.lookoutvision.model.DescribeProjectRequest;
import software.amazon.awssdk.services.lookoutvision.model.DescribeProjectResponse;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;
import software.amazon.awssdk.services.lookoutvision.model.ProjectDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Operations on Amazon Lookout for Vision projects.
public class Projects {

        public static final Logger logger = Logger.getLogger(Projects.class.getName());

        /**
         * Creates an Amazon Lookout for Vision project.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project that you want to create.
         * @return ProjectMetadata Metadata information about the created project.
         */
        public static ProjectMetadata createProject(LookoutVisionClient lfvClient, String projectName)
                        throws LookoutVisionException {

                logger.log(Level.INFO, "Creating project: {0}", projectName);
                CreateProjectRequest createProjectRequest = CreateProjectRequest.builder().projectName(projectName)
                                .build();

                CreateProjectResponse response = lfvClient.createProject(createProjectRequest);

                logger.log(Level.INFO, "Project created. ARN: {0}", response.projectMetadata().projectArn());

                return response.projectMetadata();

        }

        /**
         * Lists the Amazon Lookoutfor  Vision projects in the current AWS account and AWS
         * Region.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project that you want to create.
         * @return List<ProjectMetadata> Metadata for each project.
         */
        public static List<ProjectMetadata> listProjects(LookoutVisionClient lfvClient)
                        throws LookoutVisionException {

                logger.log(Level.INFO, "Getting projects:");
                ListProjectsRequest listProjectsRequest = ListProjectsRequest.builder()
                                .maxResults(100)
                                .build();

                List<ProjectMetadata> projectMetadata = new ArrayList<>();

                ListProjectsIterable projects = lfvClient.listProjectsPaginator(listProjectsRequest);

                projects.stream().flatMap(r -> r.projects().stream())
                                .forEach(project -> {
                                        projectMetadata.add(project);
                                        logger.log(Level.INFO, project.projectName());
                                });

                logger.log(Level.INFO, "Finished getting projects.");

                return projectMetadata;

        }

        /**
         * Deletes an Amazon Lookout for Vision project.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project that you want to create.
         * @return String The ARN of the deleted project.
         */
        public static String deleteProject(LookoutVisionClient lfvClient, String projectName)
                        throws LookoutVisionException {

                logger.log(Level.INFO, "Deleting project: {0}", projectName);

                DeleteProjectRequest deleteProjectRequest = DeleteProjectRequest.builder()
                                .projectName(projectName)
                                .build();

                DeleteProjectResponse response = lfvClient.deleteProject(deleteProjectRequest);

                logger.log(Level.INFO, "Deleted project: {0} ARN: {1}",
                                new Object[] { projectName, response.projectArn() });

                return response.projectArn();

        }

        /**
         * Gets the description for an Amazon Lookout for Vision project.
         * 
         * @param lfvClient   An Amazon Lookout for Vision client.
         * @param projectName The name of the project that you want to describe.
         * @return ProjectDescription The description for the requested project.
         */
        public static ProjectDescription describeProject(LookoutVisionClient lfvClient, String projectName)
                        throws LookoutVisionException {

                logger.log(Level.INFO, "Describing project: {0}", projectName);

                DescribeProjectRequest describeProjectRequest = DescribeProjectRequest.builder()
                                .projectName(projectName)
                                .build();

                DescribeProjectResponse describeProjectResponse = lfvClient.describeProject(describeProjectRequest);
                ProjectDescription projectDescription = describeProjectResponse.projectDescription();

                logger.log(Level.INFO, "Project: {0}%n,{1},%n,{2}%n", new Object[] {
                                projectDescription.projectName(),
                                projectDescription.projectArn(),
                                projectDescription.hasDatasets() });

                return describeProjectResponse.projectDescription();

        }

}
// snippet-end:[lookoutvision.java2.projects.complete]
