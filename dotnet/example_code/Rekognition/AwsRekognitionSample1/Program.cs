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
            AmazonRekognitionClient rekoClient = new AmazonRekognitionClient(Amazon.RegionEndpoint.USWest2);

            DetectFacesRequest dfr = new DetectFacesRequest();

            // Read image bytes and add to request
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

            // Create copy of image we can modify to highlight faces
            System.Drawing.Bitmap facesHighlighted = new System.Drawing.Bitmap(filename);
            Pen pen = new Pen(Color.Black, 3);

            using (var graphics = Graphics.FromImage(facesHighlighted))
            {
                foreach (var fd in outcome.FaceDetails)
                {
                    BoundingBox bb = fd.BoundingBox;
                    Console.WriteLine("Bounding box = (" + fd.BoundingBox.Left + ", " + fd.BoundingBox.Top + ", " +
                        fd.BoundingBox.Height + ", " + fd.BoundingBox.Width + ")");
                    graphics.DrawRectangle(pen, x: facesHighlighted.Width * bb.Left,
                        y: facesHighlighted.Height * bb.Top,
                        width: facesHighlighted.Width * bb.Width,
                        height: facesHighlighted.Height * bb.Height);
                }
            }
            facesHighlighted.Save(filename.Replace(Path.GetExtension(filename), "_faces.png"), System.Drawing.Imaging.ImageFormat.Png);
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

