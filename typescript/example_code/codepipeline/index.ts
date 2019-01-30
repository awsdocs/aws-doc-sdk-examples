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

// snippet-sourcedescription:[index.ts provides a 
// hello script in TypeScript]
// snippet-service:[codepipeline]
// snippet-keyword:[TypeScript]
// snippet-keyword:[AWS CodePipeline]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourceauthor:[AWS]
// snippet-sourcedate:[2019-1-28]
// snippet-start:[codepipeline.typescript.hello.index]
interface Greeting {
    message: string;
}

class HelloGreeting implements Greeting {
    message = "Hello!";
}

function greet(greeting: Greeting) {
    console.log(greeting.message);
}

let greeting = new HelloGreeting();

greet(greeting);
// snippet-end:[codepipeline.typescript.hello.index]