# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import json
import datetime
import time

class DateTimeEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime.datetime):
            return obj.isoformat()
        return super().default(obj)

# Bedrock Knowledge Base IDs (account 415879937535, us-west-2)
KB_IDS = {
    "python": "VJYPXZTSXT",
    "java": "64P3VU9OAD",
    "dotnet": "CRSPSCYZIX",
    "coding-standards": "Q2ZUWJJOIN",
    "steering-docs": "63A2M1LZ2E",
    "final-specs": "3KVUHEFDHK",
}

def get_or_create_data_source(knowledge_base_id, language, region_name, bedrock_agent):
    # List existing data sources
    response = bedrock_agent.list_data_sources(knowledgeBaseId=knowledge_base_id)
    data_sources = response['dataSourceSummaries']
    
    # Look for existing data source for this SDK
    for ds in data_sources:
        if language in ds['name'] and ds['name'] != "default":
            return ds['dataSourceId'], ds['name'], False  # Found existing
    if language in ["steering-docs", "final-specs"]:
        ds_name=f"{language}-data-source"
        bucket_name = f"codeloom-{language}-codeloomdev"
    else:
        ds_name=f"{language}-premium-data-source"
        bucket_name = f"codeloom-{language}-premium-codeloomdev"
    # Create new data source if none found
    response = bedrock_agent.create_data_source(
        knowledgeBaseId=knowledge_base_id,
        name=ds_name,
        dataSourceConfiguration={
            "type": "S3",
            "s3Configuration": {
                "bucketArn": f"arn:aws:s3:::{bucket_name}"
            }
        },
        vectorIngestionConfiguration = { 
            "chunkingConfiguration": { 
                "chunkingStrategy": "HIERARCHICAL",
                "hierarchicalChunkingConfiguration": { 
                    "levelConfigurations": [ 
                    { 
                        "maxTokens": 1500
                    },
                    { 
                        "maxTokens": 300
                    }
                    ],
                    "overlapTokens": 75
                }
            }
        }
    )
    return response['dataSource']['dataSourceId'], response['dataSource']['name'], True  # Created new

def sync_data_source(knowledge_base_id, data_source_id, region_name, bedrock_agent):
    response = bedrock_agent.start_ingestion_job(
        knowledgeBaseId=knowledge_base_id,
        dataSourceId=data_source_id
    )
    return response

def monitor_ingestion_job(knowledge_base_id, data_source_id, ingestion_job_id, region_name, bedrock_agent):
    max_attempts = 100
    attempts = 0
    
    while attempts < max_attempts:
        job_status = bedrock_agent.get_ingestion_job(
            knowledgeBaseId=knowledge_base_id,
            dataSourceId=data_source_id,
            ingestionJobId=ingestion_job_id
        )
        
        status = job_status['ingestionJob']['status']
        print(f"Current status: {status} - {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        
        if status in ['COMPLETE', 'FAILED', 'STOPPED']:
            return job_status
            
        attempts += 1
        time.sleep(5)
    
    return {"status": "TIMEOUT", "message": "Job monitoring timed out after 5 minutes"}

def lambda_handler(event, context):
    language = event.get('language', 'python')
    region_name = event.get('region_name', 'us-west-2')

    # Look up KB ID from hardcoded mapping
    kb_key = language if language in ["steering-docs", "final-specs", "coding-standards"] else language
    knowledge_base_id = KB_IDS.get(kb_key)
    if not knowledge_base_id:
        return {
            'statusCode': 400,
            'body': json.dumps({"error": f"No Knowledge Base configured for language: {language}"})
        }

    bedrock_agent = boto3.client('bedrock-agent', region_name=region_name)
    
    # Get or create data source
    data_source_id, data_source_name, is_new = get_or_create_data_source(
        knowledge_base_id, language, region_name, bedrock_agent
    )
    
    results = {
        "data_source": {
            "id": data_source_id,
            "name": data_source_name,
            "is_new": is_new
        },
        "ingestion_job": {},
        "statistics": None
    }
    
    # Sync the data source
    print(f"Syncing data source {data_source_name}...")
    sync_result = sync_data_source(knowledge_base_id, data_source_id, region_name, bedrock_agent)
    
    ingestion_job_id = sync_result['ingestionJob']['ingestionJobId']
    results["ingestion_job"] = {"id": ingestion_job_id, "status": "STARTED"}
    
    # Monitor the ingestion job
    final_status = monitor_ingestion_job(
        knowledge_base_id, data_source_id, ingestion_job_id, region_name, bedrock_agent
    )
    
    results["ingestion_job"]["status"] = final_status.get('ingestionJob', {}).get('status', 'UNKNOWN')
    
    # Get statistics
    if 'statistics' in final_status.get('ingestionJob', {}):
        stats = final_status['ingestionJob']['statistics']
        results["statistics"] = {
            "documents_processed": stats.get('numberOfDocumentsScanned', 0),
            "documents_failed": stats.get('numberOfDocumentsFailed', 0),
            "documents_indexed": stats.get('numberOfNewDocumentsIndexed', 0),
            "documents_modified_indexed": stats.get('numberOfModifiedDocumentsIndexed',0)
        }
    
    return {
        'statusCode': 200,
        'body': json.dumps(results, cls=DateTimeEncoder)
    }