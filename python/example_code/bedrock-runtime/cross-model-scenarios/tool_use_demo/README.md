# Amazon Bedrock Tool Use Demo

This project demonstrates a tool use scenario using Amazon Bedrock's
[Converse API](https://docs.aws.amazon.com/bedrock/latest/userguide/conversation-inference.html) and a weather tool.
The script interacts with a foundation model on Amazon Bedrock to provide weather information based on user
input. It uses the [Open-Meteo API](https://open-meteo.com/) to retrieve current weather data for a given location.

## ⚠️ Important

* Running this demo might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the
  minimum permissions required to perform the task. For more information, see
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see
  [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Project overview

This directory contains the primary script, utility scripts, and the configuration required to demonstrate the weather
assistant's functionality.

### Primary script: `tool_use_demo.py`

This primary execution script manages a conversational application using the Amazon Bedrock Converse API integrated
with a weather tool.

The application will interactively request weather information for locations specified by the user, demonstrating how
to build a chatbot that provides real-time weather information based on user queries.

- **Usage:** `python tool_use_demo.py`

### Utility scripts

- `weather_tool.py`: Provides functionality to fetch weather data from the Open-Meteo API based on geographical
  coordinates. This script defines the tool specification and implements the logic to retrieve weather data using
  latitude and longitude.
- `utils/tool_use_print_utils.py`: Contains utility functions for printing headers, footers, and other formatted
  messages to the console, enhancing the readability of logs or messages displayed during the interaction.

### Project configuration and requirements

- `requirements.txt`: Lists all Python packages that need to be installed to run the project. This file is used
  with `pip install -r requirements.txt` to ensure all dependencies are met.
- `pyproject.toml`: A configuration file that specifies project metadata and dependencies. It is compatible with various
  Python packaging tools such as pip, setuptools, pipenv, and poetry.

## Prerequisites

For general prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

> **Note:** You must request access to an AI model on Amazon Bedrock before you can use it. If you try to use the
> model (with the API or console) before you have requested access to it, you will receive an error message. For more
> information, see [Model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html).

### Install dependencies

> Depending on your operating system and how you have Python installed, the commands to install and run might vary
> slightly. For example, you may have to use `py` or `python3` in place of `python`.

To get started, you can choose one of the following options to set up the environment:

### Using `requirements.txt`

**1. Create a virtual environment:**

```shell
python -m venv .venv
```

A virtual environment is a self-contained directory that contains a Python installation and its associated packages,
ensuring that your project's dependencies are isolated from other projects.

**2. Activate the virtual environment:**

- On Linux/macOS
  ```shell
  source .venv/bin/activate
  ```

- On Windows
  ```shell
  .\.venv\Scripts\activate
  ```

Activating the virtual environment ensures that the Python interpreter and packages used in your shell session are those
from the virtual environment.

**3. Install dependencies:**

```shell
pip install -r requirements.txt
```

This command installs all the packages listed in the requirements.txt file, ensuring your project has all the necessary
dependencies.

### Alternative: Using `pyproject.toml`

Alternatively, you can use the `pyproject.toml` file with various package managers like Poetry, PDM, or Hatch. It
includes metadata about the project and a list of dependencies required for the project. Ensure you have the necessary
tool installed and follow its documentation for setting up the project environment.

## Run the demo

**1. Run the primary script:**

```shell
python tool_use_demo.py
```

**2. Follow the prompts:**

- Provide a location name or coordinates when prompted, e.g., "What's the weather like in New York?" or "Current weather
  for latitude 40.71, longitude -74.01".
- The assistant will fetch and display the current weather information using the Open-Meteo API.

> **Note:** You're not limited to single locations, or even to using English! Feel free to experiment with the app.

## Step-by-step walkthrough of the process

This demo illustrates a typical interaction between a generative AI model, an application, and connected tools or APIs
to solve a problem or achieve a specific goal. The flow follows these steps:

**Step 1: 🛠️ Initialization**

- Set up the system prompt and tool configuration.
- Specify the AI model to be used (e.g., Anthropic Claude 3 Sonnet).
- Create a client to interact with Amazon Bedrock.

**Step 2: 💬 User interaction**

- Prompt the user for an input related to the problem or goal, such as requesting weather information.
- Send the user input including the conversation history to the AI model.

**Step 3: 🤖 Request processing**

- The AI model processes the input and determines if a connected tool or API needs to be used.
- If this is the case, the model returns a tool use request with specific parameters needed to invoke the tool, and a
  unique tool use ID to correlate tool responses to the request.

**Step 4: 📡 Tool invocation**

- The application uses the tool use request to invoke the specified tool or API.
- For example, fetching weather data from an external API using provided coordinates.
- The tool's response, along with the related tool use ID, is then sent back to the AI model for further processing.

**Step 5: 🤖 Response handling**

- The AI model processes the tool's response and generates a final response.
- If the AI model requires additional information or further tool interactions, the process from steps 2 to 5 may be
  repeated.
- This ensures that the AI model has all necessary data to generate accurate and complete responses.

**Step 6. ✅ Response output**

- Once the AI model delivers the final response, the application prints the response in a clear and concise format.

This flow exemplifies how generative AI models can be integrated with applications and external tools or APIs to
effectively solve user problems and achieve specific goals.

## Additional Resources

- [Documentation: The Amazon Bedrock User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Tutorials: A developer's guide to Bedrock's new Converse API](https://community.aws/content/2dtauBCeDa703x7fDS9Q30MJoBA/amazon-bedrock-converse-api-developer-guide)
- [More examples: Amazon Bedrock code examples and scenarios in multiple programming languages](https://docs.aws.amazon.com/bedrock/latest/userguide/service_code_examples.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0