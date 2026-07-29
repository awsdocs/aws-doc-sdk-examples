#!/bin/bash
# Run SDK example tests using the pre-built Docker image for a language.
#
# Usage:
#   ./run-in-container.sh python python/example_code/ecs "pytest test/"
#   ./run-in-container.sh java javav2/example_code/ecs "mvn test"
#   ./run-in-container.sh dotnet dotnetv3/ECS "dotnet test"
#
# Arguments:
#   $1 - Language (python, java, dotnet, javascript, go, rust, ruby, php, kotlin, cpp, swift)
#   $2 - Path to example directory (relative to repo root)
#   $3 - Command to run inside the container

set -euo pipefail

LANG="$1"
EXAMPLE_PATH="$2"
CMD="$3"

ACCOUNT_ID="164794437551"
REGION="us-east-1"
REGISTRY="${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com"
IMAGE="${REGISTRY}/sdk-examples-${LANG}:latest"
REPO_ROOT="/workspace/aws-doc-sdk-examples"

# Build docker run args
DOCKER_ARGS=(
  --rm
  -v "${REPO_ROOT}:/repo:ro"
  -w "/repo/${EXAMPLE_PATH}"
  -e AWS_EC2_METADATA_DISABLED=true
)

# Pass through AWS credentials if they exist (for integration tests)
if [ -n "${AWS_ACCESS_KEY_ID:-}" ]; then
  DOCKER_ARGS+=(
    -e AWS_ACCESS_KEY_ID
    -e AWS_SECRET_ACCESS_KEY
    -e AWS_SESSION_TOKEN
    -e AWS_DEFAULT_REGION
  )
fi

echo "Running: docker run ${IMAGE} bash -c \"${CMD}\""
docker run "${DOCKER_ARGS[@]}" "${IMAGE}" bash -c "${CMD}"
