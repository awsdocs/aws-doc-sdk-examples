# passing instance id as a parameter and terminating all the instances
param(
  [alias("id")][string[]] $id_list=@()
 )
 
# terminate instance here
ForEach ($id In $id_list){
   Write-Host $id
   aws ec2 terminate-instances --instance-ids $id
}
