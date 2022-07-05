// See https://aka.ms/new-console-template for more information
using AutoScaleMVP;

Console.WriteLine("Run AutoScale MVP!");
var autoScaleOb = new AutoScalingScenario();
await autoScaleOb.PerformAutoScalingTasks(); 
