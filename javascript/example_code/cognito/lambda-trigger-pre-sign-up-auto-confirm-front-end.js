// Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// 
// This file is licensed under the Apache License, Version 2.0 (the "License").
// You may not use this file except in compliance with the License. A copy of
// the License is located at
// 
// http://aws.amazon.com/apache2.0/
// 
// This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

// snippet-sourcedescription:[This is a sample JavaScript program that demonstrates how to sign up a new user. It will invoke a pre sign-up Lambda trigger as part of the authentication.]
// snippet-service:[cognito-idp]
// snippet-keyword:[JavaScript]
// snippet-keyword:[Amazon Cognito]
// snippet-keyword:[Code Sample]
// snippet-keyword:[sign_up, update_user_attributes]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-30]
// snippet-sourceauthor:[AWS]

// snippet-start:[cognito.javascript.lambda-trigger.pre-sign-up-auto-confirm-front-end]
var attributeList = [];
var dataEmail = {
    Name : 'email',
    Value : '...' // your email here
};
var dataPhoneNumber = {
    Name : 'phone_number',
    Value : '...' // your phone number here with +country code and no delimiters in front
};

var dataEmailDomain = {
    Name: "custom:domain",
    Value: "example.com"
}
var attributeEmail = 
new AmazonCognitoIdentity.CognitoUserAttribute(dataEmail);
var attributePhoneNumber = 
new AmazonCognitoIdentity.CognitoUserAttribute(dataPhoneNumber);
var attributeEmailDomain = 
new AmazonCognitoIdentity.CognitoUserAttribute(dataEmailDomain);
 
attributeList.push(attributeEmail);
attributeList.push(attributePhoneNumber);
attributeList.push(attributeEmailDomain);
 
var cognitoUser;
userPool.signUp('username', 'password', attributeList, null, function(err, result){
    if (err) {
        alert(err);
        return;
    }
    cognitoUser = result.user;
    console.log('user name is ' + cognitoUser.getUsername());
});
// snippet-end:[cognito.javascript.lambda-trigger.pre-sign-up-auto-confirm-front-end]