# Build and manage a resilient service technical specification

This document contains the technical specifications for *Build and manage a resilient service*,
a workflow scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages. 

This document explains the following:

- Deploying AWS resources and their configurations.
- Flow of the demo and the AWS Systems Manager parameters that simulate failures and how the web 
  server responds to them. 
- Destroying the AWS resources at the end of the example.

For an introduction to *Build and manage a resilient service*, see the [README.md](README.md).

---

### Table of contents

- [Architecture](#architecture)
- [HTTP API specification](#http-api-specification)
- [User actions](#user-actions)
- [Common resources](#common-resources)
- [Deploy](#deploy)
- [Demo](#demo)
- [Destroy](#destroy)
- [Other material](#other-material)

---

## Architecture

This example sets up a web server that is load balanced and made increasingly resilient to failure during
the course of the demonstration.

1. This example relies on the default VPC hosted by Amazon Virtual Private Cloud (Amazon VPC).
The default VPC in your account should contain all the needed settings for the example to run
and provides an isolated environment for the resources used by the example.
2. An Amazon DynamoDB table acts as a service that recommends books, movies, and songs. The web
service depends on the table to make recommendations.
3. An Amazon EC2 Auto Scaling group creates and manages three EC2 instances in three separate
Availability Zones. Each instance runs a simple Python web server that handles requests, gets
recommendations from the DynamoDB table, and sends responses. The web server also responds
to health checks from the load balancer.
4. An Elastic Load Balancer provides a single endpoint that receives HTTP requests and distributes
them among the EC2 instances.
5. A set of Systems Manager parameters are used to change the behavior of the web server by
simulating failures and acting in a more resilient manner.

---

## HTTP API specification

The Python web server handles two kinds of HTTP requests. These are common to every language 
variation and do not need any additional implementation. The web server is defined in
[server.py](resources/server.py).

##### GET /

Returns a recommendation from the DynamoDB table with Amazon EC2 metadata attached that specifies which instance
handled the request.

###### Response
Content-type: application/json
```
{
  "MediaType": {"S": "Book"},
  "ItemId":{"N": "0"},
  "Title": {"S": "404 Not Found: A Coloring Book"},
  "Creator": {"S": "The Oatmeal"},
  "Metadata": {
    "InstanceId": "i-123456789",
    "AvailabilityZone": "us-west-2b"}
}
```
  
##### GET /healthcheck

Indicates to the load balancer whether the server is healthy by returning a success code (200)
or a failure code (503).

###### Response
Content-type: application/json
```
{"success": "True"}
```

---

## User actions

This example runs as a console application that goes through a series of stages that
are controlled by Systems Manager parameters. After each stage, the user is presented
with a short menu of choices:

1. Send a GET request to the load balancer endpoint.
2. Check the health of the load balancer targets. 
3. Continue to the next stage.

The user can select the first two choices multiple times to see how the situation changes as the
underlying resources update, such as when an instance becomes unhealthy.

For more detail on how this is implemented, see [Demo](#demo).

---

## Common resources

This example has a set of common resources that are stored in the [resources](resources) folder.

* [instance_policy.json](resources/instance_policy.json) contains an IAM policy that is used
  by the instance profile for each instance in this example. It grants permission to access
  the DynamoDB recommendation table and get parameters from Systems Manager.
* [recommendations.json](resources/recommendations.json) contains sample data used to populate
  the recommendation table.
* [server.py](resources/server.py) contains Python code that runs the example web server.
* [server_startup_script.sh](resources/server_startup_script.sh) contains a Bash script that
  is run when an instance starts. It installs Python packages and starts the Python web server.
* [ssm_only_policy.json](resources/ssm_only_policy.json) contains an IAM policy that is used
  to set one instance profile to a set of credentials that don't allow access to DynamoDB,
  to simulate bad credentials.

This example uses a set of Systems Manager parameters to simulate failures and control how the
web server responds to them. They must use the exact names and be set to specific values
for the example to work correctly.

* `doc-example-resilient-architecture-table` specifies the name of the DynamoDB table that the
  web server uses to get recommendations.
   * [name of your DynamoDB table]: the web server successfully gets items.
   * 'this-is-not-a-table': the web server fails to get items.
* `doc-example-resilient-architecture-failure-response` specifies how the web server responds to
  a failure to get recommendations from the table.
  * 'none': the web server returns a failure code on failure.
  * 'static': the web server returns a success code and a static JSON payload on failure.
* `doc-example-resilient-architecture-health-check` specifies how the web server responds to
  health checks.
  * 'shallow': always return a success code.
  * 'deep': return a failure code if it can't connect to the recommendation table.

---

## Deploy

The reference implementation for this example is in Python. You can find it in
[python/cross_service/resilient_service](../../python/cross_service/resilient_service).

Break deployment into three phases to make it more comprehensible to the user: introduction,
web server, and load balancing.

### Introduction

Start by introducing the workflow and asking permission to continue. This gives the user
a chance to stop the demo if they like.

```
----------------------------------------------------------------------------------------
Welcome to the demonstration of How to Build and Manage a Resilient Service!
----------------------------------------------------------------------------------------
INFO: Found credentials in shared credentials file: ~/.aws/credentials

For this demo, we'll use an AWS SDK to create several AWS resources to set up a load-balanced 
web service endpoint and explore some ways to make it resilient against various kinds of failures.

Some of the resources create by this demo are:

        * A DynamoDB table that the web service depends on to provide book, movie, and song recommendations.
        * An EC2 launch template that defines EC2 instances that each contain a Python web server.
        * An EC2 Auto Scaling group that manages EC2 instances across several Availability Zones.
        * An Elastic Load Balancing (ELB) load balancer that targets the Auto Scaling group to distribute requests.
----------------------------------------------------------------------------------------
Press Enter when you're ready to start deploying resources.
```

### Web server

#### Recommendation service

The recommendation service is a mock service and is a DynamoDB table that the web server calls 
directly. Create a DynamoDB table using DynamoDB.CreateTable and specify the following schema:

```
AttributeDefinitions=[{
    'AttributeName': 'MediaType',
    'AttributeType': 'S'},{
    'AttributeName': 'ItemId',
    'AttributeType': 'N'}],
KeySchema=[{
    'AttributeName': 'MediaType',
    'KeyType': 'HASH'}, {
    'AttributeName': 'ItemId',
    'KeyType': 'RANGE'}],
```

Populate the table by reading [recommendations.json](resources/recommendations.json) and 
sending it to DynamoDB by using DynamoDB.BatchWriteItem.

Output:

```
Creating and populating a DynamoDB table named 'doc-example-recommendation-service'.
INFO: Creating table doc-example-recommendation-service...
INFO: Table doc-example-recommendation-service created.
INFO: Populated table doc-example-recommendation-service with items from ../../../workflows/resilient_service/resources/recommendations.json.
----------------------------------------------------------------------------------------
```

#### Permissions, EC2 launch template, and Auto Scaling group

Tell the user what you're going to do:

```
Creating an EC2 launch template that runs '../../../workflows/resilient_service/resources/server_startup_script.sh' when an instance starts.
This script starts a Python web server defined in the `server.py` script. The web server
listens to HTTP requests on port 80 and responds to requests to '/' and to '/healthcheck'.
For demo purposes, this server is run as the root user. In production, the best practice is to
run a web server, such as Apache, with least-privileged credentials.

The template also defines an IAM policy that each instance uses to assume a role that grants
permissions to access the DynamoDB recommendation table and Systems Manager parameters
that control the flow of the demo.
```

##### Permissions

As you create permissions, use waiters if your SDK has them. You might also have to insert
pauses when the waiter is not sufficient. 

1. Use IAM.CreatePolicy to create an IAM policy from [instance_policy.json](resources/instance_policy.json).
2. Use IAM.CreateRole to create an IAM role.
   Specify an AssumeRolePolicyDocument that lets Amazon EC2 assume the role:
   ```
   {
     "Version": "2012-10-17",
     "Statement": [{
         "Effect": "Allow",
         "Principal": {"Service": "ec2.amazonaws.com"},
         "Action": "sts:AssumeRole"}]
   }
   ```
3. Use IAM.AttachRolePolicy to attach the policy to the role.
4. Use IAM.CreateInstanceProfile and IAM.AddRoleToInstanceProfile to create an IAM instance 
   profile and add the role.

Output:

```
INFO: Created policy with ARN arn:aws:iam::123456789012:policy/doc-example-resilience-pol.
INFO: Created role doc-example-resilience-role and attached policy arn:aws:iam::123456789012:policy/doc-example-resilience-pol.
INFO: Created profile doc-example-resilience-prof and added role doc-example-resilience-role.
----------------------------------------------------------------------------------------
```

##### EC2 launch template

1. Get the ID of a current AMI by calling SystemsManager.GetParameter for 
   `/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2`. 
2. Get the startup Bash script for the launch template by reading from
   [server_startup_script.sh](resources/server_startup_script.sh).
3. Create a launch template by calling EC2.CreateLaunchTemplate. Specify a small instance type,
   such as 't3.micro', the ID of the AMI, the instance profile name, and the start script
   as UserData. You must encode the UserData as base64.
   ```
   LaunchTemplateData={
       'InstanceType': 't3.micro',
       'ImageId': ami_id,
       'IamInstanceProfile': {'Name': self.instance_profile_name},
       'UserData': base64.b64encode(start_server_script.encode(encoding='utf-8')).decode(encoding='utf-8')})
   ```

Output:

```
INFO: Created launch template doc-example-resilience-template for AMI ami-0167739a362a4b484 on t3.micro.
----------------------------------------------------------------------------------------
```

##### Auto Scaling group

1. Use EC2.DescribeAvailabilityZones to get the names of the Availability Zones for the 
current Region.
2. Use AutoScaling.CreateAutoScalingGroup to create an Auto Scaling group that uses the launch template,
targets the Availability Zones for the Region, and specifies three minimum and maximum instances.
   ```
   AvailabilityZones=zones,
   LaunchTemplate={
       'LaunchTemplateName': launch_template_name, 'Version': '$Default'},
   MinSize=group_size, MaxSize=group_size)
   ```

Output:

```
Creating an EC2 Auto Scaling group that maintains three EC2 instances, each in a different
Availability Zone.
INFO: Created EC2 Auto Scaling group doc-example-resilience-template with availability zones ['us-west-2a', 'us-west-2b', 'us-west-2c', 'us-west-2d'].
----------------------------------------------------------------------------------------
At this point, you have EC2 instances created. After each instance starts, it listens for
HTTP requests. You can see these instances in the console or continue with the demo.
----------------------------------------------------------------------------------------
```

Pause at this point to let the user read about what's happened, and to give the instances a
chance to start.

### Load balancer

Elastic Load Balancer has two clients. This example uses an Application Load Balancer, so it
targets V2. Be aware that the Auto Scaling group is not the same as the load balancer
target group, although the two are linked.

1. Use EC2.DescribeVpcs with a Filter of Name = `'is-default'` and Values = `['true']` to 
get the default VPC.
2. Use EC2.DescribeSubnets with a Filter to get the default subnets for the VPC.
   ```
   Filters=[
       {'Name': 'vpc-id', 'Values': [vpc_id]},
       {'Name': 'availability-zone', 'Values': zones},
       {'Name': 'default-for-az', 'Values': ['true']}])
   ```
3. Use ELBv2.CreateTargetGroup to create the target group for the load balancer.
   Specify the arguments shown in the following snippet:
   ```
   response = self.elb_client.create_target_group(
       Name=self.target_group_name, 
       Protocol='HTTP', 
       Port=80,
       HealthCheckPath='/healthcheck', 
       HealthCheckIntervalSeconds=10,
       HealthCheckTimeoutSeconds=5, 
       HealthyThresholdCount=2,
       UnhealthyThresholdCount=2, 
       VpcId=vpc_id)
   ```
4. Use ELBv2.CreateLoadBalancer to create an Application Load Balancer. Specify the default 
subnets for Subnets.
5. Cache the DNSName field of the load balancer. This is the endpoint where you will send
   GET requests for the example.
6. Use ELBv2.CreateListener to add a listener that forwards requests from the load balancer
   endpoint to the target group:
   ```
   self.elb_client.create_listener(
       LoadBalancerArn=load_balancer['LoadBalancerArn'],
       Protocol=target_group['Protocol'],
       Port=target_group['Port'],
       DefaultActions=[{'Type': 'forward', 'TargetGroupArn': target_group['TargetGroupArn']}])
   ```
7. Use AutoScaling.AttachLoadBalancerTargetGroups to associate the Auto Scaling group
with the load balancer target group. This completes the linkage between the load balancer
and the instances in the Auto Scaling group.

Output:

```
Creating an Elastic Load Balancing target group and load balancer. The target group
defines how the load balancer connects to instances. The load balancer provides a
single endpoint where clients connect and dispatches requests to instances in the group.

INFO: Found 4 subnets for the specified zones.
INFO: Created load balancing target group doc-example-resilience-tg.
INFO: Created load balancer doc-example-resilience-lb.
INFO: Waiting for load balancer to be available...
INFO: Load balancer is available!
INFO: Created listener to forward traffic from load balancer doc-example-resilience-lb to target group doc-example-resilience-tg.
INFO: Attached load balancer target group doc-example-resilience-tg to auto scaling group doc-example-resilience-group.
```

#### Verify endpoint

1. Verify that the load balancer endpoint responds to requests by sending it a GET request using
   an appropriate HTTP client. You might want to retry a few times with a pause between tries,
   in order to give the system a chance to settle.
   ```
   lb_response = requests.get(f'http://{self.endpoint()}')
   ```
2. If the request succeeds, display that to the user along with the endpoint.
3. If the request fails, the most likely culprit is the default security group for the VPC.
   Use EC2.DescribeSecurityGroups to get the default security group and examine its 
   IpPermissions to find whether it has port 80 open either to the current computer's IP 
   address (you can find this programmatically by sending a GET request to http://checkip.amazonaws.com),
   to all IP addresses (0.0.0.0/0) or to a VPN/Corpnet prefix:
    ```
    response = self.ec2_client.describe_security_groups(
        Filters=[
            {'Name': 'group-name', 'Values': ['default']},
            {'Name': 'vpc-id', 'Values': [vpc['VpcId']]}])
    sec_group = response['SecurityGroups'][0]
    port_is_open = False
    log.info("Found default security group %s.", sec_group['GroupId'])
    for ip_perm in sec_group['IpPermissions']:
        if ip_perm.get('FromPort', 0) == port:
            log.info("Found inbound rule: %s", ip_perm)
            for ip_range in ip_perm['IpRanges']:
                cidr = ip_range.get('CidrIp', '')
                if cidr.startswith(ip_address) or cidr == '0.0.0.0/0':
                    port_is_open = True
            if ip_perm['PrefixListIds']:
                port_is_open = True
            if not port_is_open:
                log.info(
                    "The inbound rule does not appear to be open to either this computer's IP\n"
                    "address of %s, to all IP addresses (0.0.0.0/0), or to a prefix list ID.", ip_address)
            else:
                break
    ```
5. If there is not a rule, ask the user if they want to add one for the current IP address:
    ```
    if q.ask(f"Do you want to add a rule to security group {sec_group['GroupId']} to allow\n"
          f"inbound traffic on port {self.port} from your computer's IP address of {current_ip_address}? (y/n) ",
          q.is_yesno):
        self.ec2_client.authorize_security_group_ingress(
            GroupId=sec_group_id, CidrIp=f'{ip_address}/32', FromPort=port, ToPort=port, IpProtocol='tcp')
    ```
6. Try again to send a GET request to the endpoint. If this still fails, bail out and tell
   the customer to do some troubleshooting of their own.

Output when a rule exists but the request fails:

```
Verifying access to the load balancer endpoint...
INFO: Got connection error from load balancer endpoint, retrying...
INFO: Got connection error from load balancer endpoint, retrying...
INFO: Got connection error from load balancer endpoint, retrying...
Couldn't connect to the load balancer, verifying that the port is open...
INFO: Found default security group sg-0bf0814e.
INFO: Found inbound rule: {'FromPort': 80, 'IpProtocol': 'tcp', 'IpRanges': [], 'Ipv6Ranges': [], 'PrefixListIds': [{'PrefixListId': 'pl-123456789'}], 'ToPort': 80, 'UserIdGroupPairs': []}
INFO: Got connection error from load balancer endpoint, retrying...
INFO: Got connection error from load balancer endpoint, retrying...
INFO: Got connection error from load balancer endpoint, retrying...
Couldn't get a successful response from the load balancer endpoint. Troubleshoot by
manually verifying that your VPC and security group are configured correctly and that
you can successfully make a GET request to the load balancer endpoint:

        http://doc-example-resilience-lb-1234567890.us-west-2.elb.amazonaws.com
----------------------------------------------------------------------------------------
```

## Demo

The demo phase of this example cycles through several stages, setting Systems Manager
parameters along the way to simulate failures and instruct the web server to take increasingly
resilient actions.

### Choices

After each stage, present the user with three choices:

```
See the current state of the service by selecting one of the following choices:

1. Send a GET request to the load balancer endpoint.
2. Check the health of load balancer targets.
3. Go to the next part of the demo.

Which action would you like to take?
```

#### 1. Send a GET request

Send a GET request to the load balancer endpoint. Depending on the stage of the demo, the 
response is either successful and contains a JSON payload that contains the recommendation 
and server metadata, or fails and returns a failure code.

```
Request:

GET http://doc-example-resilience-lb-1282105285.us-west-2.elb.amazonaws.com

Response:

200
{'Title': {'S': '12 Angry Men'},
 'Creator': {'S': 'Sidney Lumet'},
 'MediaType': {'S': 'Movie'},
 'ItemId': {'N': '3'},
 'Metadata': {'InstanceId': 'i-03ca37214fa45fe33',
              'AvailabilityZone': 'us-west-2a'}}
----------------------------------------------------------------------------------------
```

#### 2. Check health

Use ELBv2.DescribeTargetHealth to get the health of the load balancer targets.

```
Checking the health of load balancer targets:

        Target i-03ca37214fa45fe33 on port 80 is healthy
        Target i-087544ae95640b911 on port 80 is healthy
        Target i-0fa75af663ba14821 on port 80 is healthy

Note that it can take a minute or two for the health check to update
after changes are made.

----------------------------------------------------------------------------------------
```

#### 3. Next stage

Move to the next stage of the demo and show the choice menu again.

### Stages

The following are the stages of the demo, which must be performed in the order shown.

#### Set parameters

Before you start, use SSM.PutParameter to set the Systems Manager parameters to the starting 
values. The names of the parameters must exactly match these names, because they are used by 
the web server to get the parameters at runtime.
```
table = 'doc-example-resilient-architecture-table'
failure_response = 'doc-example-resilient-architecture-failure-response'
health_check = 'doc-example-resilient-architecture-health-check'

self.ssm_client.put_parameter(Name=table, Value=<name of your DynamoDB table>, Overwrite=True)
self.ssm_client.put_parameter(Name=failure_response, Value='none', Overwrite=True)
self.ssm_client.put_parameter(Name=health_check, Value='shallow', Overwrite=True)
```

#### Initial state

At the beginning, the recommendation service successfully responds and all instances are healthy.

```bash
----------------------------------------------------------------------------------------
Request:

GET http://doc-example-resilience-lb-1317068782.us-west-2.elb.amazonaws.com

Response:

200
{'Title': {'S': 'Pride and Prejudice'},
 'Creator': {'S': 'Jane Austen'},
 'MediaType': {'S': 'Book'},
 'ItemId': {'N': '1'},
 'Metadata': {'InstanceId': 'i-05387127cb2ebbea1',
              'AvailabilityZone': 'us-west-2c'}}
----------------------------------------------------------------------------------------
```

```bash
----------------------------------------------------------------------------------------

Checking the health of load balancer targets:

        Target i-02d98d9d0726c4b2d on port 80 is healthy
        Target i-0e4b7104cfaf8e056 on port 80 is healthy
        Target i-05387127cb2ebbea1 on port 80 is healthy
----------------------------------------------------------------------------------------
```

#### Broken dependency

The next phase simulates a broken dependency by setting the table name parameter to a 
non-existent table name. When the web server tries to get a recommendation, it fails because 
the table doesn't exist.

Use SSM.PutParameter to set the `doc-example-resilient-architecture-table` parameter to a value
other than the name of your DynamoDB recommendation table, such as `this-is-not-a-table`.

```
The web service running on the EC2 instances gets recommendations by querying a DynamoDB table.
The table name is contained in a Systems Manager parameter named 'doc-example-resilient-architecture-table'.
To simulate a failure of the recommendation service, let's set this parameter to name a non-existent table.

INFO: Setting demo parameter doc-example-resilient-architecture-table to 'this-is-not-a-table'.

Now, sending a GET request to the load balancer endpoint returns a failure code. But, the service reports as
healthy to the load balancer because shallow health checks don't check for failure of the recommendation service.
----------------------------------------------------------------------------------------
```

Response to GET:

```bash
----------------------------------------------------------------------------------------
Request:

GET http://doc-example-resilience-lb-1317068782.us-west-2.elb.amazonaws.com

Response:

502
----------------------------------------------------------------------------------------
```

All instances report as healthy because they use shallow health checks, which means 
that they simply report success under all conditions.

#### Static response

The next phase sets a parameter that instructs the web server to return a static response when 
it cannot get a recommendation from the recommendation service. The static response is to always 
suggest the *404 Not Found* coloring book.

Use SSM.PutParameter to set the `doc-example-resilient-architecture-failure-response` parameter
to `static`.

```
Instead of failing when the recommendation service fails, the web service can return a static response.
While this is not a perfect solution, it presents the customer with a somewhat better experience than failure.

INFO: Setting demo parameter doc-example-resilient-architecture-failure-response to 'static'.

Now, sending a GET request to the load balancer endpoint returns a static response.
The service still reports as healthy because health checks are still shallow.

----------------------------------------------------------------------------------------
```

Response to GET request:

```bash
Request:

GET http://doc-example-resilience-lb-1317068782.us-west-2.elb.amazonaws.com

Response:

200
{'MediaType': {'S': 'Book'},
 'ItemId': {'N': '0'},
 'Title': {'S': '404 Not Found: A Coloring Book'},
 'Creator': {'S': 'The Oatmeal'},
 'Metadata': {'InstanceId': 'i-05387127cb2ebbea1',
              'AvailabilityZone': 'us-west-2c'}}
----------------------------------------------------------------------------------------
```

#### Bad credentials

The next phase replaces the credentials on a single instance with credentials that don't allow 
access to the recommendation service.

Use SSM.PutParameter to set the `doc-example-resilient-architecture-table` parameter back to 
the name of your DynamoDB recommendation table.

##### Create an instance profile with bad credentials

Create all the pieces needed for an instance profile that does not allow permission to the
DynamoDB recommendation table. 

1. Use IAM.CreatePolicy to create an IAM policy from [ssm_only_policy.json](resources/ssm_only_policy.json).
2. Use IAM.CreateRole to create an IAM role.
   Specify an AssumeRolePolicyDocument that lets EC2 assume the role:
   ```
   {
     "Version": "2012-10-17",
     "Statement": [{
         "Effect": "Allow",
         "Principal": {"Service": "ec2.amazonaws.com"},
         "Action": "sts:AssumeRole"}]
   }
   ```
3. Use IAM.AttachRolePolicy to attach the policy to the role.
4. Use IAM.AttachRolePolicy to attach the managed policy `AmazonSSMManagedInstanceCore' to the role.
   This is required so that Systems Manager can restart the web server on the instance.
5. Use IAM.CreateInstanceProfile and IAM.AddRoleToInstanceProfile to create an IAM instance 
   profile and add the role.

Select an instance, replace its instance profile, and reboot the instance.

1. Use AutoScaling.DescribeAutoScalingGroups to get the instance IDs for the group. Pick one
   to poison.
2. Use EC2.DescribeIamInstanceProfileAssociations to get the profile association ID for the
   instance.
3. Use EC2.ReplaceIamInstanceProfileAssociation to replace the profile for the instance with 
   the new instance profile that contains bad credentials.
4. Use EC2.RebootInstances to reboot the instance.
5. Use SSM.DescribeInstanceInformation in a loop (with pauses) until the instance is in the
   returned list. This indicates that the instance can receive Systems Manager commands.
6. Use SSM.SendCommand to restart the web server on the instance.
   ```
   self.ssm_client.send_command(
       InstanceIds=[instance_id], 
       DocumentName='AWS-RunShellScript',
       Parameters={'commands': ['cd / && sudo python3 server.py 80']})
   ```

Tell the user all about it:

```
Let's reinstate the recommendation service.

INFO: Setting demo parameter doc-example-resilient-architecture-table to 'doc-example-recommendation-service'.

Let's also substitute bad credentials for one of the instances in the target group so that it can't
access the DynamoDB recommendation table.

INFO: Created policy with ARN arn:aws:iam::123456789012:policy/doc-example-resilience-bc-pol.
INFO: Created role doc-example-resilience-bc-role and attached policy arn:aws:iam::123456789012:policy/doc-example-resilience-bc-pol.
INFO: Created profile doc-example-resilience-bc-prof and added role doc-example-resilience-bc-role.

Replacing the profile for instance i-03ca37214fa45fe33 with a profile that contains
bad credentials...

INFO: Replaced instance profile for association iip-assoc-05ceb4b8735f72381 with profile doc-example-resilience-bc-prof.
INFO: Rebooting instance i-03ca37214fa45fe33 and waiting for it to to be ready.
INFO: Restarted the Python web server on instance i-03ca37214fa45fe33.
Now, sending a GET request to the load balancer endpoint returns either a recommendation or a static response,
depending on which instance is selected by the load balancer.

----------------------------------------------------------------------------------------
```

An instance on us-west-2a gives real recommendations:

```bash
----------------------------------------------------------------------------------------
Request:

GET http://doc-example-resilience-lb-1317068782.us-west-2.elb.amazonaws.com

Response:

200
{'Title': {'S': 'Delicatessen'},
 'Creator': {'S': 'Jeunet et Caro'},
 'MediaType': {'S': 'Movie'},
 'ItemId': {'N': '1'},
 'Metadata': {'InstanceId': 'i-02d98d9d0726c4b2d',
              'AvailabilityZone': 'us-west-2a'}}
----------------------------------------------------------------------------------------
```

While the bad instance on us-west-2b gives a static response:

```bash
----------------------------------------------------------------------------------------
Request:

GET http://doc-example-resilience-lb-1317068782.us-west-2.elb.amazonaws.com

Response:

200
{'MediaType': {'S': 'Book'},
 'ItemId': {'N': '0'},
 'Title': {'S': '404 Not Found: A Coloring Book'},
 'Creator': {'S': 'The Oatmeal'},
 'Metadata': {'InstanceId': 'i-0e4b7104cfaf8e056',
              'AvailabilityZone': 'us-west-2b'}}
----------------------------------------------------------------------------------------
```

#### Deep health checks

The next phase sets a parameter that instructs the web server to use a deep health check.
This means that the web server returns an error code when it can't connect to the recommendations 
service.

Note that the deep health check is only for ELB routing and not for Auto Scaling instance health. 
This kind of deep health check is not recommended for Auto Scaling instance health, see 
[Choosing the right health check with Elastic Load Balancing and EC2 Auto Scaling](https://aws.amazon.com/blogs/networking-and-content-delivery/choosing-the-right-health-check-with-elastic-load-balancing-and-ec2-auto-scaling/) 
for more information.

Use SSM.PutParameter to set the `doc-example-resilient-architecture-health-check` to 'deep'.

Output:

```
Let's implement a deep health check. For this demo, a deep health check tests whether
the web service can access the DynamoDB table that it depends on for recommendations. Note that
the deep health check is only for ELB routing and not for Auto Scaling instance health.
This kind of deep health check is not recommended for Auto Scaling instance health, because it
risks accidental termination of all instances in the Auto Scaling group when a dependent service fails.

By implementing deep health checks, the load balancer can detect when one of the instances is failing
and take that instance out of rotation.

INFO: Setting demo parameter doc-example-resilient-architecture-health-check to 'deep'.

Now, checking target health indicates that the instance with bad credentials (i-041efe367831f49b5)
is unhealthy. Note that it might take a minute or two for the load balancer to detect the unhealthy
instance. Sending a GET request to the load balancer endpoint always returns a recommendation, because
the load balancer takes unhealthy instances out of its rotation.

----------------------------------------------------------------------------------------
```

After this change, the instance with bad credentials reports as unhealthy:

```bash
----------------------------------------------------------------------------------------

Checking the health of load balancer targets:

        Target i-02d98d9d0726c4b2d on port 80 is healthy
        Target i-0e4b7104cfaf8e056 on port 80 is unhealthy
                Target.ResponseCodeMismatch: Health checks failed with these codes: [503]

        Target i-05387127cb2ebbea1 on port 80 is healthy

----------------------------------------------------------------------------------------
```

#### Replace the failing instance

This next phase uses an SDK action to terminate the unhealthy instance, at which point Auto Scaling 
automatically starts a new instance.

Use AutoScaling.TerminateInstanceInAutoScalingGroup with ShouldDecrementDesiredCapacity=False
to stop the instance with bad credentials.

Output:

```
Because the instances in this demo are controlled by an auto scaler, the simplest way to fix an unhealthy
instance is to terminate it and let the auto scaler start a new instance to replace it.

INFO: Terminated instance i-041efe367831f49b5.

Even while the instance is terminating and the new instance is starting, sending a GET
request to the web service continues to get a successful recommendation response because
the load balancer routes requests to the healthy instances. After the replacement instance
starts and reports as healthy, it is included in the load balancing rotation.

Note that terminating and replacing an instance typically takes several minutes, during which time you
can see the changing health check status until the new instance is running and healthy.

----------------------------------------------------------------------------------------
```

While the instances are transitioning, you will see various results from the health check, for example:

```bash
----------------------------------------------------------------------------------------

Checking the health of load balancer targets:

        Target i-02d98d9d0726c4b2d on port 80 is healthy
        Target i-05387127cb2ebbea1 on port 80 is healthy
        Target i-0e4b7104cfaf8e056 on port 80 is draining
                Target.DeregistrationInProgress: Target deregistration is in progress

        Target i-0c8df865e77bbb943 on port 80 is unhealthy
                Target.FailedHealthChecks: Health checks failed

----------------------------------------------------------------------------------------
```

After the new instance starts, it reports as healthy and is again included in the load balancer's rotation.

#### Fail open

This last phase of the example again sets the table name parameter to a non-existent table to simulate a failure of the
recommendation service. This causes all instances to report as unhealthy.

Use SSM.PutParameter to set the `doc-example-resilient-architecture-table` parameter to a value
other than the name of your DynamoDB recommendation table, such as `this-is-not-a-table`.

Output:

```
If the recommendation service fails now, deep health checks mean all instances report as unhealthy.

INFO: Setting demo parameter doc-example-resilient-architecture-table to 'this-is-not-a-table'.

When all instances are unhealthy, the load balancer continues to route requests even to
unhealthy instances, allowing them to fail open and return a static response rather than fail
closed and report failure to the customer.
----------------------------------------------------------------------------------------
```

Health check now shows all instances are unhealthy:

```bash
----------------------------------------------------------------------------------------

Checking the health of load balancer targets:

        Target i-02d98d9d0726c4b2d on port 80 is unhealthy
                Target.ResponseCodeMismatch: Health checks failed with these codes: [503]

        Target i-05387127cb2ebbea1 on port 80 is unhealthy
                Target.ResponseCodeMismatch: Health checks failed with these codes: [503]

        Target i-0c8df865e77bbb943 on port 80 is unhealthy
                Target.ResponseCodeMismatch: Health checks failed with these codes: [503]

----------------------------------------------------------------------------------------
```

When all instances in a target group are unhealthy, the load balancer continues to forward requests to 
them, allowing for a fail open behavior.

## Destroy

After the demo portion of the example, give the user the option to destroy all resources,
and then do so. Use waiters as necessary if your SDK provides them.

1. Use ELBv2.DeleteLoadBalancer to delete the load balancer. Wait for it to be deleted.
2. Use ELBv2.DeleteTargetGroup to delete the load balancer target group. You might get a
   'ResourceInUse' error, in which case you'll have to wait and try again.
3. Use AutoScaling.UpdateAutoScalingGroup to have MinSize=0.
4. Use AutoScaling.TerminateInstanceInAutoScalingGroup to terminate all instances in the group.
   Wait for all instances to terminate. This is required before you can delete the Auto Scaling group.
5. Use AutoScaling.DeleteAutoScalingGroup to delete the Auto Scaling group. You might get
   a 'ScalingActivityInProgress' error, in which case you'll have to wait and try again.
6. Use EC2.DeleteLaunchTemplate to delete the launch template.
7. Do the following steps for the main profile and the one with bad credentials:
   1. Use IAM.RemoveRoleFromInstanceProfile to remove the role from the instance profile.
   2. Use IAM.DeleteInstanceProfile to delete the instance profile.
   3. Use IAM.ListAttachedRolePolicies to get all policies attached to the role.
   4. Use IAM.DetachRolePolicy and IAM.DeletePolicy to detach and delete each policy. Don't delete
      AWS managed policies, which have ARNs that start with 'arn:aws:iam::aws'. 
   5. Use IAM.DeleteRole to delete the role.
8. Use DynamoDB.DeleteTable to delete the recommendations table.

Output:

```
This concludes the demo of how to build and manage a resilient service.
To keep things tidy and to avoid unwanted charges on your account, we can clean up all AWS resources
that were created for this demo.
Do you want to clean up all demo resources? (y/n) y
INFO: Deleted load balancer doc-example-resilience-lb.
INFO: Waiting for load balancer to be deleted...
INFO: Target group not yet released from load balancer, waiting...
INFO: Deleted load balancing target group doc-example-resilience-tg.
INFO: Stopping i-041efe367831f49b5.
INFO: Stopping i-08022d9ebb1041b55.
INFO: Stopping i-08763def19ccbcbd6.
INFO: Stopping i-0f2c8709826fe8bf6.
INFO: Some instances are still running. Waiting for them to stop...
INFO: Some instances are still running. Waiting for them to stop...
INFO: Some instances are still running. Waiting for them to stop...
INFO: Some instances are still running. Waiting for them to stop...
INFO: Some instances are still running. Waiting for them to stop...
INFO: Some instances are still running. Waiting for them to stop...
INFO: Some instances are still running. Waiting for them to stop...
INFO: Deleted EC2 Auto Scaling group doc-example-resilience-group.
INFO: Deleted instance profile doc-example-resilience-prof.
INFO: Detached and deleted policy doc-example-resilience-pol.
INFO: Deleted role doc-example-resilience-role.
INFO: Launch template doc-example-resilience-template deleted.
INFO: Deleted instance profile doc-example-resilience-bc-prof.
INFO: Detached and deleted policy doc-example-resilience-bc-pol.
INFO: Detached and deleted policy AmazonSSMManagedInstanceCore.
INFO: Deleted role doc-example-resilience-bc-role.
INFO: Deleting table doc-example-recommendation-service...
INFO: Table doc-example-recommendation-service deleted.
----------------------------------------------------------------------------------------
Thanks for watching!
----------------------------------------------------------------------------------------
```

---

## Hello Service

Most services used in this example already have an MVP defined. The only new service
to add is Elastic Load Balancing.

* ELBv2.DescribeLoadBalancers. List LoadBalancerName and DNSName of up to 10 load balancers. 
There might not be any if the customer has not yet defined any.

Output:

```
Hello, Elastic Load Balancing! Let's list some of your load balancers:
        test-load-balancer: test-load-balancer-1921885376.us-west-2.elb.amazonaws.com
```

---

## Actions

**Elastic Load Balancing V2**

* `DescribeLoadBalancers`
* `CreateTargetGroup`
* `DescribeTargetGroups`
* `DeleteTargetGroup`
* `CreateLoadBalancer`
* `CreateListener`
* `DeleteLoadBalancer`
* `DescribeTargetHealth`

**Amazon EC2 Auto Scaling**

In addition to the actions implemented as part of the MVP for Amazon EC2 Auto Scaling:

`AttachLoadBalancerTargetGroup`

This example implements several actions that overlap with the actions for the MVP.
If you find that the actions for this example differ enough from the MVP actions, add
them as a second excerpt (with differentiating description) to the existing example.

**IAM**

* `CreateInstanceProfile`
* `DeleteInstanceProfile`

**EC2**

* `DescribeIamInstanceProfileAssociations`
* `ReplaceIamInstancePorfileAssociation`
* `CreateaLaunchTemplate`
* `DeleteLaunchTemplate`
* `DescribeVpcs`
* `DescribeSubnets`

---

## Metadata

**elastic-load-balancing-v2_metadata.yaml**

* elastic-load-balancing-v2_DescribeLoadBalancers
* elastic-load-balancing-v2_CreateTargetGroup
* elastic-load-balancing-v2_DescribeTargetGroups
* elastic-load-balancing-v2_DeleteTargetGroup
* elastic-load-balancing-v2_CreateLoadBalancer
* elastic-load-balancing-v2_CreateListener
* elastic-load-balancing-v2_DeleteLoadBalancer
* elastic-load-balancing-v2_DescribeTargetHealth

**auto-scaling_metadata.yaml**

* auto-scaling_AttachLoadBalancerTargetGroup

**iam_metadata.yaml**

* iam_CreateInstanceProfile
* iam_DeleteInstanceProfile

**ec2_metadata.yaml**

* ec2_DescribeIamInstanceProfileAssociations
* ec2_ReplaceIamInstancePorfileAssociation
* ec2_CreateaLaunchTemplate
* ec2_DeleteLaunchTemplate
* ec2_DescribeVpcs
* ec2_DescribeSubnets

---

# Other material

If technical details are not what you seek, try these instead:

* [High-level summary](README.md)
* [Community.aws: How to build and manage a resilient service using AWS SDKs](https://community.aws/posts/build-and-manage-a-resilient-service-using-aws-sdks)

