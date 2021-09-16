/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 2 (v2).
This example is in the Amazon Rekognition Developer Guide' at
https://docs.aws.amazon.com/rekognition/latest/dg/image-bytes-javascript.html.

Purpose:
estimate-age.html is part of an example that demonstrates how to use Amazon Rekognition to estimate the ages of faces in an image.
To view the full example, see https://docs.aws.amazon.com/rekognition/latest/dg/image-bytes-javascript.html.

Inputs :
   - REGION
*/

//snippet-start:[rekognition.JavaScript.alternative.detect_faces_v2]
function ProcessImage() {
    AnonLog();
    var control = document.getElementById("fileToUpload");
    var file = control.files[0];

    // Load base64 encoded image for display.
    var reader = new FileReader();
    reader.onload = (function (theFile) {
        return function (e) {
            //Call Rekognition
            AWS.region = "REGION";
            var rekognition = new AWS.Rekognition();
            var params = {
                Image: {
                    Bytes: e.target.result
                },
                Attributes: [
                    'ALL',
                ]
            };
            rekognition.detectFaces(params, function (err, data) {
                if (err) console.log(err, err.stack); // an error occurred
                else {
                    var table = "<table><tr><th>Low</th><th>High</th></tr>";
                    // show each face and build out estimated age table
                    for (var i = 0; i < data.FaceDetails.length; i++) {
                        table += '<tr><td>' + data.FaceDetails[i].AgeRange.Low +
                            '</td><td>' + data.FaceDetails[i].AgeRange.High + '</td></tr>';
                    }
                    table += "</table>";
                    document.getElementById("opResult").innerHTML = table;
                }
            });

        };
    })(file);
    reader.readAsArrayBuffer(file);
}

//snippet-end:[rekognition.JavaScript.alternative.detect_faces_v2]

//Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//PDX-License-Identifier: MIT-0 (For details, see https://github.com/awsdocs/amazon-rekognition-developer-guide/blob/master/LICENSE-SAMPLECODE.)
