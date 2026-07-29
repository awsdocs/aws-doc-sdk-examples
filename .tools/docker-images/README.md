# SDK Code Example Docker Images

Per-language Docker images for running AWS SDK code examples in AgentSpaces.
Each image contains the language runtime, build tools, and common dependencies pre-installed.

## Architecture

```
ECR: 164794437551.dkr.ecr.us-east-1.amazonaws.com/
├── sdk-examples-python:latest
├── sdk-examples-java:latest
├── sdk-examples-dotnet:latest
├── sdk-examples-javascript:latest
├── sdk-examples-go:latest
├── sdk-examples-rust:latest
├── sdk-examples-ruby:latest
├── sdk-examples-php:latest
├── sdk-examples-kotlin:latest
├── sdk-examples-cpp:latest
└── sdk-examples-swift:latest
```

## Usage in AgentSpaces

```bash
# 1. Authenticate to ECR
export AWS_EC2_METADATA_DISABLED=true
eval "$(ada credentials print --account 164794437551 --role brianUser --provider isengard --format env)"
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 164794437551.dkr.ecr.us-east-1.amazonaws.com

# 2. Pull the language image
docker pull 164794437551.dkr.ecr.us-east-1.amazonaws.com/sdk-examples-python:latest

# 3. Run tests (unit tests — no AWS creds needed)
docker run --rm \
  -v /workspace/aws-doc-sdk-examples:/repo:ro \
  164794437551.dkr.ecr.us-east-1.amazonaws.com/sdk-examples-python:latest \
  bash -c "cd /repo/python/example_code/ecs && pip install -q -r requirements.txt && pytest test/"

# 4. Run integration tests (pass AWS creds through)
docker run --rm \
  -v /workspace/aws-doc-sdk-examples:/repo:ro \
  -e AWS_ACCESS_KEY_ID \
  -e AWS_SECRET_ACCESS_KEY \
  -e AWS_SESSION_TOKEN \
  -e AWS_DEFAULT_REGION=us-east-1 \
  -e AWS_EC2_METADATA_DISABLED=true \
  164794437551.dkr.ecr.us-east-1.amazonaws.com/sdk-examples-python:latest \
  bash -c "cd /repo/python/example_code/ecs && pip install -q -r requirements.txt && pytest test/ --integration"
```

## Image Rebuild Automation

Images should be rebuilt weekly (or on dependency changes) via a CI pipeline.
See `buildspec.yml` for the CodeBuild configuration.
