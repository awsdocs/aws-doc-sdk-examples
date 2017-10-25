/*******************************************************************************
* Copyright 2009-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License"). You may
* not use this file except in compliance with the License. A copy of the
* License is located at
*
* http://aws.amazon.com/apache2.0/
*
* or in the "license" file accompanying this file. This file is
* distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the specific
* language governing permissions and limitations under the License.
*******************************************************************************/

using System;
using System.Drawing;
using System.IO;
using Amazon.Rekognition;
using Amazon.Rekognition.Model;

namespace NETRekognitionConsole
{
    class Program
    {
        static void IdentifyFaces(string filename)
        {
            // Using USWest2, not the default region
            AmazonRekognitionClient rekoClient = new AmazonRekognitionClient(Amazon.RegionEndpoint.USWest2);

            DetectFacesRequest dfr = new DetectFacesRequest();

            // Request needs image butes, so read and add to request
            Amazon.Rekognition.Model.Image img = new Amazon.Rekognition.Model.Image();
            byte[] data = null;
            using (FileStream fs = new FileStream(filename, FileMode.Open, FileAccess.Read))
            {
                data = new byte[fs.Length];
                fs.Read(data, 0, (int)fs.Length);
            }
            img.Bytes = new MemoryStream(data);
            dfr.Image = img;
            var outcome = rekoClient.DetectFaces(dfr);

            // Load a bitmap to modify with face bounding box rectangles
            System.Drawing.Bitmap facesHighlighted = new System.Drawing.Bitmap(filename);
            Pen pen = new Pen(Color.Black, 3);

            // Create a graphics context
            using (var graphics = Graphics.FromImage(facesHighlighted))
            {
                foreach (var fd in outcome.FaceDetails)
                {
                    // Get the bounding box
                    BoundingBox bb = fd.BoundingBox;
                    Console.WriteLine("Bounding box = (" + fd.BoundingBox.Left + ", " + fd.BoundingBox.Top + ", " +
                        fd.BoundingBox.Height + ", " + fd.BoundingBox.Width + ")");
                    // Draw the rectangle using the bounding box values
                    // They are percentages so scale them to picture
                    graphics.DrawRectangle(pen, x: facesHighlighted.Width * bb.Left,
                        y: facesHighlighted.Height * bb.Top,
                        width: facesHighlighted.Width * bb.Width,
                        height: facesHighlighted.Height * bb.Height);
                }
            }
            // Save the image with highlights as PNG
            string fileout = filename.Replace(Path.GetExtension(filename), "_faces.png");
            facesHighlighted.Save(fileout, System.Drawing.Imaging.ImageFormat.Png);
            Console.WriteLine(">>> Faces highlighted in file " + fileout);
        }

        static void Main(string[] args)
        {
            if (args.Length != 1)
            {
                Console.WriteLine(
                    "AwsRekognitionSample1 <image>\n" +
                    "   Identifies faces from source image with bounding rectangles\n" +
                    "   and lists properties\n" +
                    "Arguments: " +
                    "   <image> - image in jpg/png/gif" +
                    "Example:\n" +
                    "   Rekodemo image01.png\n");
                return;
            }
            var filename = System.IO.Path.Combine(Environment.CurrentDirectory, args[0]);
            IdentifyFaces(filename);
        }
    }
}

