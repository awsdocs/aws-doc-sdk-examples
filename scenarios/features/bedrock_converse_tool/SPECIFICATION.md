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
This process can be repeated as needed. See the .NET implementation for an example of the processing of the messages. Following is an example of how the conversation could go:

1. **Greet the user and provide an overview of the application**:
  - The application is an assistant that provides current weather information for user-specified locations.
  - Users can ask for weather details by providing the location name or coordinates.
  - Example queries are provided, such as "What's the weather like in New York?" and "Current weather for latitude 40.70, longitude -74.01".
  - Users can exit the application by typing 'x' and pressing Enter.
  - The application is not limited to single locations or using English.

2. **Handle the user's weather information request**:
  - The user requests weather information.
  - The application looks up the latitude and longitude coordinates for Oklahoma City.
  - The application then uses the Weather_Tool to retrieve the current weather data for those coordinates.
  - The application prints the current weather conditions, including the temperature, wind speed and direction, and a description of the weather.

3. **Handle an off-topic user request**:
  - The user requests information about a different topic.
  - The application responds that it is focused on providing current weather information and does not have any data or capabilities related to discussing other topics.
  - The application suggests returning to discussing weather conditions for a particular location.

4. **Find the warmest city in a location**:
  - The user requests the warmest city in a state.
  - The application looks up the coordinates for some major cities in the state.
  - The application uses the Weather_Tool to retrieve the current temperature for each city.
  - The application compares the temperatures prints a response.

5 **Exit the application**:
  - The user types 'x' and presses Enter to exit the application.
  - The application prints a farewell message and provides a link to more Bedrock examples.


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
| `Converse`                                 | bedrock-runtime_metadata.yaml  | bedrock-runtime_Converse_AnthropicClaude             |
| `Scenario: Tool use with the Converse API` | bedrock-runtime_metadata.yaml  | bedrock-runtime_Scenario_ToolUseDemo_AnthropicClaude |

