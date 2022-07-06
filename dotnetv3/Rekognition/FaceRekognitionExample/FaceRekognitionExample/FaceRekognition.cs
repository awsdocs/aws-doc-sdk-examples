// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace FaceRekognitionExample
{
    // snippet-start:[Rekognition.dotnetv3.FaceRekognitionExample]
    using System;
    using System.Drawing;
    using System.IO;
    using System.Threading.Tasks;
    using Amazon.Rekognition;
    using Amazon.Rekognition.Model;

    /// <summary>
    /// This examples uses the Amazon Rekognition service to scan an image for
    /// faces and then scans the same file looking for celebrity faces. The
    /// results are stored in {filename}_faces.png and {filename}_celebrityfaces.png
    /// respectively. Faces will be marked with a bounding box and celebrity
    /// names are added to {filename}_celebrityfaces.png if any are found.
    /// </summary>
    public class FaceRekognition
    {
        public static async Task Main(string[] args)
        {
            AmazonRekognitionClient rekoClient = new AmazonRekognitionClient();

            if (args.Length != 1)
            {
                Console.WriteLine(
                    "AwsRekognitionSample1 <image>\n" +
                    "   Identifies faces from source image with bounding rectangles\n" +
                    "   and lists properties.\n" +
                    "Arguments: " +
                    "   <image> - full path to image in jpg/png/gif format." +
                    "Example:\n" +
                    "   FaceRekognitionExample image01.png\n");
                return;
            }

            var filename = args[0];

            if (File.Exists(filename))
            {
                await IdentifyFaces(rekoClient, filename);
                await IdentifyCelebrityFaces(rekoClient, filename);
            }
        }

        /// <summary>
        /// Identifies faces in the image file. If faces are found, the
        /// method adds bounding boxes.
        /// </summary>
        /// <param name="client">The Rekognition client used to call
        /// RecognizeCelebritiesAsync.</param>
        /// <param name="filename">The name of the file that potentially
        /// contins images of celebrities.</param>
        public static async Task IdentifyFaces(AmazonRekognitionClient client, string filename)
        {
            // Request needs image bytes, so read and add to request.
            byte[] data = File.ReadAllBytes(filename);

            DetectFacesRequest request = new DetectFacesRequest
            {
                Image = new Amazon.Rekognition.Model.Image
                {
                    Bytes = new MemoryStream(data),
                },
            };

            DetectFacesResponse response = await client.DetectFacesAsync(request);

            if (response.FaceDetails.Count > 0)
            {
                // Load a bitmap to modify with face bounding box rectangles.
                Bitmap facesHighlighted = new Bitmap(filename);
                Pen pen = new Pen(Color.Black, 3);

                // Create a graphics context.
                using (var graphics = System.Drawing.Graphics.FromImage(facesHighlighted))
                {
                    foreach (var fd in response.FaceDetails)
                    {
                        // Get the bounding box.
                        BoundingBox bb = fd.BoundingBox;
                        Console.WriteLine($"Bounding box = ({bb.Left}, {bb.Top}, {bb.Height}, {bb.Width})");

                        // Draw the rectangle using the bounding box values.
                        // They are percentages so scale them to the picture.
                        graphics.DrawRectangle(
                            pen,
                            x: facesHighlighted.Width * bb.Left,
                            y: facesHighlighted.Height * bb.Top,
                            width: facesHighlighted.Width * bb.Width,
                            height: facesHighlighted.Height * bb.Height);
                    }
                }

                // Save the image with highlights as PNG.
                string fileout = filename.Replace(Path.GetExtension(filename), "_faces.png");
                facesHighlighted.Save(fileout, System.Drawing.Imaging.ImageFormat.Png);

                Console.WriteLine(">>> " + response.FaceDetails.Count + " face(s) highlighted in file " + fileout);
            }
            else
            {
                Console.WriteLine(">>> No faces found");
            }
        }

        /// <summary>
        /// Scans the contents of an image file looking for celebrities. If any
        /// are found, a bounding box will be drawn around the face and the name
        /// of the celebrity drawn under the box.
        /// </summary>
        /// <param name="client">The Rekognition client used to call
        /// RecognizeCelebritiesAsync.</param>
        /// <param name="filename">The name of the file that potentially
        /// contins faces.</param>
        public static async Task IdentifyCelebrityFaces(AmazonRekognitionClient client, string filename)
        {
            // Request needs image bytes, so read and add to request.
            byte[] data = File.ReadAllBytes(filename);

            RecognizeCelebritiesRequest request = new RecognizeCelebritiesRequest
            {
                Image = new Amazon.Rekognition.Model.Image
                {
                    Bytes = new MemoryStream(data),
                },
            };

            RecognizeCelebritiesResponse response = await client.RecognizeCelebritiesAsync(request);

            if (response.CelebrityFaces.Count > 0)
            {
                // Load a bitmap to modify with face bounding box rectangles.
                Bitmap facesHighlighted = new Bitmap(filename);
                Pen pen = new Pen(Color.Black, 3);
                Font drawFont = new Font("Arial", 12);

                // Create a graphics context.
                using (var graphics = Graphics.FromImage(facesHighlighted))
                {
                    foreach (var fd in response.CelebrityFaces)
                    {
                        // Get the bounding box.
                        BoundingBox bb = fd.Face.BoundingBox;
                        Console.WriteLine($"Bounding box = ({bb.Left}, {bb.Top}, {bb.Height}, {bb.Width})");

                        // Draw the rectangle using the bounding box values.
                        // They are percentages so scale them to the picture.
                        graphics.DrawRectangle(
                            pen,
                            x: facesHighlighted.Width * bb.Left,
                            y: facesHighlighted.Height * bb.Top,
                            width: facesHighlighted.Width * bb.Width,
                            height: facesHighlighted.Height * bb.Height);
                        graphics.DrawString(
                            fd.Name,
                            font: drawFont,
                            brush: Brushes.White,
                            x: facesHighlighted.Width * bb.Left,
                            y: (facesHighlighted.Height * bb.Top) + (facesHighlighted.Height * bb.Height));
                    }
                }

                // Save the image with highlights as PNG.
                string fileout = filename.Replace(Path.GetExtension(filename), "_celebrityfaces.png");
                facesHighlighted.Save(fileout, System.Drawing.Imaging.ImageFormat.Png);

                Console.WriteLine($">>> {response.CelebrityFaces.Count} celebrity face(s) highlighted in file {fileout}.");

                Console.WriteLine("Found the following celebritie(s):");
                foreach (var celeb in response.CelebrityFaces)
                {
                    Console.WriteLine($"{celeb.Name}");
                }
            }
            else
            {
                Console.WriteLine(">>> No celebrity faces found");
            }
        }
    }
    // snippet-end:[Rekognition.dotnetv3.FaceRekognitionExample]
}
