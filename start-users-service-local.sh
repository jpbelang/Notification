#!/bin/bash
set -e

# Get the script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# This script builds the users-service and starts it locally using AWS SAM.
# It uses the CDK-synthesized template to define the local environment.

echo ">>> Building users-service shadow JAR..."
./gradlew :users:users-service:shadowJar

echo ">>> Synthesizing infrastructure with CDK..."
cd users/users-infrastructure
npx cdk synth --quiet

echo ">>> Starting SAM local API..."
echo "Note: This requires Docker to be running."
sam local start-api -t cdk.out/UsersInfrastructureStack.template.json --env-vars users-awsenv.json
