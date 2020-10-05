// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
// snippet-start:[rekognition.dotNET.DetectFaces]
using System;
using System;
using System.Drawing;
using System.IO;
using Amazon;
using Amazon.Rekognition;
using Amazon.Rekognition.Model;

namespace NETRekognitionConsole
{
    class Program
    {
        static void IdentifyFaces(string filename)
        {
            // Using USWest2, not the default Region
            AmazonRekognitionClient rekoClient = new AmazonRekognitionClient(Amazon.RegionEndpoint.USWest2);

            // Request needs image bytes, so read and add to request
            byte[] data = File.ReadAllBytes(filename);

            DetectFacesRequest dfr = new DetectFacesRequest
            {
                Image = new Amazon.Rekognition.Model.Image
                {
                    Bytes = new MemoryStream(data)
                }
            };

            DetectFacesResponse outcome = rekoClient.DetectFaces(dfr);

            if (outcome.FaceDetails.Count > 0)
            {
                // Load a bitmap to modify with face bounding box rectangles
                Bitmap facesHighlighted = new Bitmap(filename);
                Pen pen = new Pen(Color.Black, 3);

                // Create a graphics context
                using (var graphics = Graphics.FromImage(facesHighlighted))
                {
                    foreach (var fd in outcome.FaceDetails)
                    {
                        // Get the bounding box
                        BoundingBox bb = fd.BoundingBox;
                        Console.WriteLine($"Bounding box = ({bb.Left}, {bb.Top}, {bb.Height}, {bb.Width})");

                        // Draw the rectangle using the bounding box values
                        // They are percentages so scale them to picture
                        graphics.DrawRectangle(pen,
                            x: facesHighlighted.Width * bb.Left,
                            y: facesHighlighted.Height * bb.Top,
                            width: facesHighlighted.Width * bb.Width,
                            height: facesHighlighted.Height * bb.Height);
                    }
                }

                // Save the image with highlights as PNG
                string fileout = filename.Replace(Path.GetExtension(filename), "_faces.png");
                facesHighlighted.Save(fileout, System.Drawing.Imaging.ImageFormat.Png);

                Console.WriteLine(">>> " + outcome.FaceDetails.Count + " face(s) highlighted in file " + fileout);
            }
            else
            {
                Console.WriteLine(">>> No faces found");
            }
        }

        static void IdentifyCelebrityFaces(string filename)
        {
            // Using USWest2, not the default region
            AmazonRekognitionClient rekoClient = new AmazonRekognitionClient(Amazon.RegionEndpoint.USWest2);
            
            // Request needs image bytes, so read and add to request
            byte[] data = File.ReadAllBytes(filename);

            RecognizeCelebritiesRequest rcr = new RecognizeCelebritiesRequest
            {
                Image = new Amazon.Rekognition.Model.Image
                {
                    Bytes = new MemoryStream(data)
                }
            };

            RecognizeCelebritiesResponse outcome = rekoClient.RecognizeCelebrities(rcr);

            if (outcome.CelebrityFaces.Count > 0)
            {
                // Load a bitmap to modify with face bounding box rectangles
                Bitmap facesHighlighted = new Bitmap(filename);
                Pen pen = new Pen(Color.Black, 3);
                Font drawFont = new Font("Arial", 12);

                // Create a graphics context
                using (var graphics = Graphics.FromImage(facesHighlighted))
                {
                    foreach (var fd in outcome.CelebrityFaces)
                    {
                        // Get the bounding box
                        BoundingBox bb = fd.Face.BoundingBox;
                        Console.WriteLine($"Bounding box = ({bb.Left}, {bb.Top}, {bb.Height}, {bb.Width})");

                        // Draw the rectangle using the bounding box values
                        // They are percentages so scale them to picture
                        graphics.DrawRectangle(pen,
                            x: facesHighlighted.Width * bb.Left,
                            y: facesHighlighted.Height * bb.Top,
                            width: facesHighlighted.Width * bb.Width,
                            height: facesHighlighted.Height * bb.Height);
                        graphics.DrawString(fd.Name,
                            font: drawFont,
                            brush: Brushes.White,
                            x: facesHighlighted.Width * bb.Left,
                            y: facesHighlighted.Height * bb.Top + facesHighlighted.Height * bb.Height);
                    }
                }

                // Save the image with highlights as PNG
                string fileout = filename.Replace(Path.GetExtension(filename), "_celebrityfaces.png");
                facesHighlighted.Save(fileout, System.Drawing.Imaging.ImageFormat.Png);

                Console.WriteLine(">>> " + outcome.CelebrityFaces.Count + " celebrity face(s) highlighted in file " + fileout);
            }
            else
            {
                Console.WriteLine(">>> No celebrity faces found");
            }
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
            var filename = args[0];
            IdentifyFaces(filename);
            IdentifyCelebrityFaces(filename);
        }
    }
}
// snippet-end:[rekognition.dotNET.DetectFaces]
