import {SageMakerClient,SendPipelineExecutionStepFailureCommand,SendPipelineExecutionStepSuccessCommand}from'@aws-sdk/client-sagemaker';import {SageMakerGeospatialClient,ExportVectorEnrichmentJobCommand,GetVectorEnrichmentJobCommand,VectorEnrichmentJobStatus,StartVectorEnrichmentJobCommand}from'@aws-sdk/client-sagemaker-geospatial';/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */


/**
 * @typedef { {
 *   vej_name?: string,
 *   vej_config?: import('@aws-sdk/client-sagemaker-geospatial').VectorEnrichmentJobConfig,
 *   vej_input_config?: import('@aws-sdk/client-sagemaker-geospatial').VectorEnrichmentJobInputConfig,
 *   role?: string
 * } } GeospatialPipelineEvent
 */

/**
 * @typedef {GeospatialPipelineEvent | import('@types/aws-lambda').SQSEvent} HandlerEvent
 */

/**
 * @param {HandlerEvent} event
 */
async function handler(event) {
  const sagemakerGeospatialClient = new SageMakerGeospatialClient({
    region: "us-west-2",
  });
  const sagemakerClient = new SageMakerClient({ region: "us-west-2" });

  if ("vej_name" in event) {
    console.info("Starting VectorEnrichmentJob");
    return await startVectorEnrichmentJob(sagemakerGeospatialClient, {
      name: event.vej_name,
      jobConfig: JSON.parse(event.vej_config),
      inputConfig: JSON.parse(event.vej_input_config),
      role: event.role,
    });
  }

  if ("vej_export_config" in event) {
    console.info("Starting ExportVectorEnrichmentJob");
    const outputConfig = JSON.parse(event.vej_export_config);
    const response = await sagemakerGeospatialClient.send(
      new ExportVectorEnrichmentJobCommand({
        Arn: event.vej_arn,
        ExecutionRoleArn: event.role,
        OutputConfig: outputConfig,
      })
    );
    console.info(
      "ExportVectorEnrichmentJob response",
      JSON.stringify(response)
    );
    return {
      export_vej_status: response.ExportStatus,
      vej_arn: response.Arn,
    };
  }

  if ("Records" in event) {
    for (const record of event.Records) {
      /**
       * @type {{ arguments: {role: string, region: string, vej_arn: string}, token: string, pipelineExecutionArn: string, status: string}}
       */
      const body = JSON.parse(record.body);

      const { Status, ErrorDetails } = await sagemakerGeospatialClient.send(
        new GetVectorEnrichmentJobCommand({
          Arn: body.arguments.vej_arn,
        })
      );

      switch (Status) {
        case VectorEnrichmentJobStatus.COMPLETED:
          await sagemakerClient.send(
            new SendPipelineExecutionStepSuccessCommand({
              CallbackToken: body.token,
              OutputParameters: [{ Name: "export_status", Value: Status }],
            })
          );
          break;
        case VectorEnrichmentJobStatus.FAILED:
          await sagemakerClient.send(
            new SendPipelineExecutionStepFailureCommand({
              CallbackToken: body.token,
              FailureReason: ErrorDetails.ErrorMessage,
            })
          );
          break;
        case VectorEnrichmentJobStatus.IN_PROGRESS:
          throw new Error("Vector Enrichment Job is still in progress");
      }
    }
  }
}

/**
 * @param {import('@aws-sdk/client-sagemaker-geospatial').SageMakerGeospatialClient} client
 * @param {{
 *   name: string,
 *   jobConfig: import('@aws-sdk/client-sagemaker-geospatial').VectorEnrichmentJobConfig,
 *   inputConfig: import('@aws-sdk/client-sagemaker-geospatial').VectorEnrichmentJobInputConfig,
 *   role: string
 * }} startJobCommandProps
 * @returns {Promise<{vej_arn?: string, statusCode?: string}>}
 */
async function startVectorEnrichmentJob(
  client,
  { name, jobConfig, inputConfig, role }
) {
  const command = new StartVectorEnrichmentJobCommand({
    Name: name,
    JobConfig: jobConfig,
    InputConfig: inputConfig,
    ExecutionRoleArn: role,
  });

  const { Arn: vej_arn } = await client.send(command);

  return { vej_arn };
}export{handler};