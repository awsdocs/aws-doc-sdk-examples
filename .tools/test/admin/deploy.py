import subprocess
import yaml

def run_shell_command(command):
    """Run a shell command and return its output"""
    try:
        output = subprocess.check_output(command, shell=True, stderr=subprocess.STDOUT)
        print(f"Command output: {output.decode()}")
    except subprocess.CalledProcessError as e:
        print(f"Error executing command: {e.output.decode()}")

def deploy_resources(account_id, account_name):
    """Deploy resources to the given account"""
    # Get AWS tokens for the account
    get_tokens_command = f"ada credentials update --account {account_id} --provider isengard --role weathertop-cdk-deployments --once"
    run_shell_command(get_tokens_command)

    export_name_command = f"export TOOL_NAME={account_name}"
    run_shell_command(export_name_command)

    # Deploy CDK stack to the account
    # deploy_command = "cdk deploy --require-approval never"
    deploy_command = "cdk list"
    run_shell_command(deploy_command)

def main():
    # Load account IDs from YAML file
    with open('../config/targets.yaml', 'r') as file:
        accounts = yaml.safe_load(file)
    
    for account_name, account_info in accounts.items():
        print(f"Deploying to account {account_name} with ID {account_info['account_id']}")
        deploy_resources(account_info['account_id'], account_name)

if __name__ == "__main__":
    main()

