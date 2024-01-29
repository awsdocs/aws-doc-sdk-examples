// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[medical-imaging.JavaScript.createclientv3]
import { MedicalImagingClient } from "@aws-sdk/client-medical-imaging";

// The AWS Region can be provided here using the `region` property. If you leave it blank
// the SDK will default to the region set in your AWS config.
export const medicalImagingClient = new MedicalImagingClient();
// snippet-end:[medical-imaging.JavaScript.createclientv3]
