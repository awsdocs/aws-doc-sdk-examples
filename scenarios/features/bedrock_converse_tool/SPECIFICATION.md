# Bedrock Runtime Converse API with Tool Feature Scenario - Technical specification

This document contains the technical specifications for _Bedrock Runtime Converse API with Tool Feature Scenario_, a feature scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code examples team to use while developing this example in additional languages.

This document explains the following:

- Architecture and features of the example scenario.
- Metadata information for the scenario.
- Sample reference output.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [User Input](#user-input)
- [Example Output](#example-output)
- [Errors](#errors)
- [Metadata](#metadata)

## User Input

The user's input is used as the starting point for the Bedrock Runtime conversation, and each response is added to an array of messages. 
The model should respond when it needs to invoke the tool, and the application should run the tool and append the response to the conversation.
This process can be repeated as needed until a maximum number of recursions (5). See the .NET implementation for an example of the processing of the messages. Following is an example of how the conversation could go:

1. Greet the user and provide an overview of the application.
1. Handle the user's weather information request:
   1. The user requests weather information. This request is sent to the Bedrock model.
   2. The model response includes a tool request, with a latitude and longitude to provide to the tool.
   3. The application then uses the Weather_Tool to retrieve the current weather data for those coordinates, and appends that response as a tool response to the conversation. The conversation is sent back to the model.
   4. The model responds with either a final response, or a request for more information. The process repeats. 
   5. The application prints the final response.
1. Any off topic requests should be handled according to the system prompt. This prompt is provided below.
1. The user can type 'x' to exit the application.

#### System prompt
```
You are a weather assistant that provides current weather data for user-specified locations using only
the Weather_Tool, which expects latitude and longitude. Infer the coordinates from the location yourself.
If the user provides coordinates, infer the approximate location and refer to it in your response.
To use the tool, you strictly apply the provided tool specification.

- Explain your step-by-step process, and give brief updates before each step.
- Only use the Weather_Tool for data. Never guess or make up information. 
- Repeat the tool use for subsequent requests if necessary.
- If the tool errors, apologize, explain weather is unavailable, and suggest other options.
- Report temperatures in °C (°F) and wind in km/h (mph). Keep weather reports concise. Sparingly use
  emojis where appropriate.
- Only respond to weather queries. Remind off-topic users of your purpose. 
- Never claim to search online, access external data, or use tools besides Weather_Tool.
- Complete the entire process until you have all required data before sending the complete response.
```

#### Weather tool specification
For strongly typed languages, you will need to use the Bedrock classes provided for tool specification.

```
"toolSpec": {
            "name": "Weather_Tool",
            "description": "Get the current weather for a given location, based on its WGS84 coordinates.",
            "inputSchema": {
                "json": {
                    "type": "object",
                    "properties": {
                        "latitude": {
                            "type": "string",
                            "description": "Geographical WGS84 latitude of the location.",
                        },
                        "longitude": {
                            "type": "string",
                            "description": "Geographical WGS84 longitude of the location.",
                        },
                    },
                    "required": ["latitude", "longitude"],
                }
            },
        }
```


## Example Output
```
********************************************************************************
        Welcome to the Amazon Bedrock Tool Use demo!
********************************************************************************

        This assistant provides current weather information for user-specified locations.
        You can ask for weather details by providing the location name or coordinates.

        Example queries:
        - What's the weather like in New York?
        - Current weather for latitude 40.70, longitude -74.01
        - Is it warmer in Rome or Barcelona today?

        To exit the program, simply type 'x' and press Enter.

        P.S.: You're not limited to single locations, or even to using English!
        Have fun and experiment with the app!

********************************************************************************
        Your weather info request: (x to exit):

>What's the weather like in Oklahoma City?
        Calling Bedrock...
        The model's response:

Okay, let me get the current weather information for Oklahoma City:

1) I will look up the latitude and longitude coordinates for Oklahoma City.
2) Then I will use the Weather_Tool to get the weather data for those coordinates.


        Invoking tool: Weather_Tool with input: 35.4676, -97.5164...

        Calling Bedrock...
        The model's response:

According to the weather data, the current conditions in Oklahoma City are:

??? Partly cloudy
Temperature: 2.7°C (36.9°F)
Wind: 22.3 km/h (13.9 mph) from the North

The wind is breezy and it's a bit cool for this time of year in Oklahoma City. I'd recommend wearing a jacket if going outside for extended periods.

********************************************************************************
        Your weather info request: (x to exit):

>What's the best kind of cat?
        Calling Bedrock...
        The model's response:

I'm an AI assistant focused on providing current weather information using the available Weather_Tool. I don't have any data or capabilities related to discussing different types of cats. Perhaps we could return to discussing weather conditions for a particular location? I'd be happy to look up the latest forecast if you provide a city or geographic coordinates.

********************************************************************************
        Your weather info request: (x to exit):

>Where is the warmest city in Oklahoma right now?
        Calling Bedrock...
        The model's response:

Okay, let me see if I can find the warmest city in Oklahoma right now using the Weather_Tool:

1) I will look up the coordinates for some major cities in Oklahoma.
2) Then I will use the Weather_Tool to get the current temperature for each city.
3) I will compare the temperatures to determine the warmest city.


        Invoking tool: Weather_Tool with input: 35.4676, -97.5164...

        Calling Bedrock...
        The model's response:

Oklahoma City: 2.7°C


        Invoking tool: Weather_Tool with input: 36.1539, -95.9925...

        Calling Bedrock...
        The model's response:

Tulsa: 5.5°C

Based on the data from the Weather_Tool, the warmest major city in Oklahoma right now is Tulsa at 5.5°C (41.9°F).

********************************************************************************
        Your weather info request: (x to exit):

>What's the warmest city in California right now?
        Calling Bedrock...
        The model's response:

OK, let me check the current temperatures in some major cities in California to find the warmest one:


        Invoking tool: Weather_Tool with input: 34.0522, -118.2437...

        Calling Bedrock...
        The model's response:

Los Angeles: 10.6°C (51.1°F)


        Invoking tool: Weather_Tool with input: 37.7749, -122.4194...

        Calling Bedrock...
        The model's response:



San Francisco: 11.6°C (52.9°F)


        Invoking tool: Weather_Tool with input: 32.7157, -117.1611...

        Calling Bedrock...
        Warning: Maximum number of recursions reached. Please try again.
        The model's response:

San Diego: 12.9°C (55.2°F)

Based on the data from the Weather_Tool, the warmest major city in California right now appears to be San Diego at 12.9°C (55.2°F).

********************************************************************************
        Your weather info request: (x to exit):
>x
********************************************************************************
        Thank you for checking out the Amazon Bedrock Tool Use demo. We hope you
        learned something new, or got some inspiration for your own apps today!

        For more Bedrock examples in different programming languages, have a look at:
        https://docs.aws.amazon.com/bedrock/latest/userguide/service_code_examples.html
********************************************************************************

Amazon  Bedrock Converse API with Tool Use Feature Scenario is complete.
--------------------------------------------------------------------------------

```
- Cleanup
  - There are no resources needing cleanup in this scenario.

---

## Errors
In addition to handling Bedrock Runtime errors on the Converse action, the scenario should also
handle errors related to the tool itself, such as an HTTP Request failure.

| action         | Error                  | Handling                                             |
|----------------|------------------------|------------------------------------------------------|
| `Converse`     | ModelNotReady          | Notify the user to try again, and stop the scenario. |
| `HTTP Request` | HttpRequestException   | Notify the user and stop the scenario.               |

---

## Metadata
For languages which already have an entry for the action, add a description for the snippet describing the scenario or action.

| action / scenario                          | metadata file                  | metadata key                                         |
|--------------------------------------------|--------------------------------|------------------------------------------------------|
| `Converse`                                 | bedrock-runtime_metadata.yaml  | bedrock-runtime_Converse_AmazonNovaText             |
| `Tool use with the Converse API`           | bedrock-runtime_metadata.yaml  | bedrock-runtime_Scenario_ToolUse                     |
| `Scenario: Tool use with the Converse API` | bedrock-runtime_metadata.yaml  | bedrock-runtime_Scenario_ToolUseDemo_AmazonNova |


