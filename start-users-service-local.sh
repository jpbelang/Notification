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

# Try to update users-awsenv.json with real values from the deployed stack
echo ">>> Attempting to update users-awsenv.json with real values from AWS..."
STACK_NAME="UsersInfrastructureStack"
OUTPUTS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[0].Outputs" --output json 2>/dev/null || echo "null")

if [ "$OUTPUTS" != "null" ]; then
    TABLE_NAME=$(echo $OUTPUTS | jq -r '.[] | select(.OutputKey=="UsersTableName") | .OutputValue')
    POOL_ID=$(echo $OUTPUTS | jq -r '.[] | select(.OutputKey=="CognitoUserPoolId") | .OutputValue')
    FUNCTION_NAME=$(echo $OUTPUTS | jq -r '.[] | select(.OutputKey=="UsersHandlerName") | .OutputValue')
    
    # Get Logical ID from the template (should match what was written by CDK, but safer to check)
    HANDLER_LOGICAL_ID=$(jq -r '.Resources | to_entries | .[] | select(.value.Type=="AWS::Lambda::Function" and (.key | startswith("UsersHandler"))) | .key' cdk.out/$STACK_NAME.template.json)

    cat <<EOF > users-awsenv.json
{
  "$HANDLER_LOGICAL_ID": {
    "DYNAMODB_TABLE_NAME": "$TABLE_NAME",
    "COGNITO_USER_POOL_ID": "$POOL_ID",
    "LAMBDA_FUNCTION_NAME": "$FUNCTION_NAME"
  }
}
EOF
    echo ">>> Updated users-awsenv.json with real values."
else
    echo ">>> WARNING: Could not fetch stack outputs. Using placeholder values in users-awsenv.json."
fi

echo ">>> Starting SAM local API..."
echo "Note: This requires Docker to be running."
sam local start-api -t cdk.out/UsersInfrastructureStack.template.json --env-vars users-awsenv.json
