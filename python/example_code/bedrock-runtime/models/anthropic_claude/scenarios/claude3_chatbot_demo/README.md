# README for Claude3 Chatbot Demo

This project showcases two conversational applications utilizing AWS Bedrock integrated with the LangChain library. Each script simulates interactive conversations leveraging different contexts and resources, such as user-provided documents and knowledge bases.

## Execution Scripts
This directory contains two versions of a chatbot built on AWS Bedrock.

### 1. `with_document.py`
Manages a conversational application using a document retrieved from AWS S3 as context for the interaction. This script exemplifies how to integrate external document contexts into conversational flows.
  - **Usage:** `python with_document.py`
  - By default, this application will load the resume of Albert Einstein; however, you can change this behavior by modifying the source code.

### 2. `with_knowledgebase.py`
Facilitates a conversational QA application that leverages a knowledge base along with a large language model to generate contextually relevant answers based on user queries.
  - **Usage:** `python with_knowledgebase.py --knowledge_base_id 123456` (replace with a valid knowledge base ID)
  - By default, this will fail until you successfully configure a Knowledge Base.

## Configuration and Requirements
- **`config.yml`:** A YAML file that stores configuration settings such as AWS credentials, model IDs, bucket names, etc., required for the operation of scripts interacting with AWS services.
- **`requirements.txt`:** Lists all Python packages that need to be installed to run the project. This file is used with `pip install -r requirements.txt` to ensure all dependencies are met.

## Utility Scripts
- **`colors.py`:** Provides functions to add color to console outputs, enhancing the readability and visual appeal of logs or messages displayed in the terminal.
- **`custom_logging.py`:** Implements custom logging functionalities, allowing for more structured and configurable logging throughout the project.
- **`timeit.py`:** Contains a decorator to measure and log the execution time of functions. This utility is helpful for performance analysis and optimization.
- **`upload_document.py`:** Designed to upload resume documents to an Amazon S3 bucket. It is used to provide the document context required by the `with_document.py` script for conversation simulation.

## Pre-Requisites
To get started:
1. Run `pip install -r requirements.txt` & activate the virtual environment.
2. Set up AWS tokens.
3. Run `python utils/upload_document.py` (required for `with_document.py`).
4. Create a [Knowledge Base for Amazon Bedrock](https://aws.amazon.com/bedrock/knowledge-bases/) (required for `with_knowledgebase.py`).
