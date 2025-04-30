// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[location.JavaScript.CalculateRouteV3]
import { fileURLToPath } from "node:url";
import {
  CalculateRouteCommand,
  LocationClient,
  ResourceNotFoundException,
} from "@aws-sdk/client-location";

import data from "./inputs.json" with { type: "json" };
const region = "eu-west-1";
const locationClient = new LocationClient({ region: region });
const calculateRouteParams = {
  CalculatorName: `${data.inputs.calculatorName}`,
  DeparturePosition: [-122.3321, 47.6062],
  DestinationPosition: [-123.1216, 49.2827],
  TravelMode: "Car",
  DistanceUnit: "Kilometers",
};
export const main = async () => {
  try {
    const command = new CalculateRouteCommand(calculateRouteParams);
    const response = await locationClient.send(command);

    console.log(
      "Successfully calculated route. The distance in kilometers is : ",
      response.Summary.Distance,
    );
  } catch (caught) {
    if (caught instanceof ResourceNotFoundException) {
      console.error(
        `An conflict occurred: ${caught.message} \n Exiting program.`,
      );
      return;
    }
  }
};

// snippet-end:[location.JavaScript.CalculateRouteV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
