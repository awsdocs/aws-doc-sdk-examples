# passing instance id as a parameter
param (
    [string]$id = ""
)

# aws cli must be installed
$ipString = aws ec2 describe-instances --instance-ids $id --query 'Reservations[].Instances[].PublicIpAddress' 
$ipString = $ipString[1]
$ipString = $ipString -replace '"' , ""
$ip =  $ipString.Trim()
Write-Host $ip
