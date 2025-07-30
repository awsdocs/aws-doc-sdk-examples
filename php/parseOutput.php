<?php

$file = file_get_contents("output.txt");
if(!$file){
    echo "{\"json\": false}";
    die();
}

$output = [
    "schema-version" => "0.0.1",
    "results" => [
        "tool" => "",
        "summary" => [
            "tests" => "",
            "passed" => "",
            "failed" => "",
            "start_time" => 0,
            "stop_time" => "",
        ],
        "tests" => [
            [
                "name" => "test1",
                "status" => "passed",
                "duration" => 1,
                "message" => "passed",
                "log" => "none",
            ],
        ],
    ],
];

$by_loc = strpos($file, " by ");
$tool = substr($file, 0, $by_loc);

$runtime_loc = strpos($file,"Runtime:");
$configuration_loc = strpos($file, "Configuration:", $runtime_loc);
$version = trim(substr($file, $runtime_loc + 8, $configuration_loc - $runtime_loc - 8));

$output['results']['tool'] = $tool;

$time_loc = strpos($file, "Time:");
$memory_loc = strpos($file, "Memory:", $time_loc);
$time = trim(substr($file, $time_loc + 5, $memory_loc - $time_loc - 7));
$timeInSeconds = strtotime("1970-01-01T00:".$time, 0); // #TODO add support for hour+ long runs

$output['results']['summary']['stop_time'] = $timeInSeconds;

// Check for OK, which is all passed
$ok_loc = strpos($file, "OK (");
if($ok_loc !== false) {
    $tests_loc = strpos($file, "tests,", $ok_loc);
    $total_tests = trim(substr($file, $ok_loc + 4, $tests_loc - $ok_loc - 4));

    $output['results']['summary']['tests'] = (int)$total_tests;
    $output['results']['summary']['passed'] = (int)$total_tests;
    $output['results']['summary']['failed'] = 0;
}else{
    //look for failure state
    $tests_loc = strpos($file, "Tests: ");
    $end_tests_loc = strpos($file, ",", $tests_loc);
    $total_tests = substr($file, $tests_loc + 6, $end_tests_loc - $tests_loc - 6);

    $assertions_loc = strpos($file, "Assertions: ", $tests_loc);
    $end_assertions_loc = strpos($file, ",", $assertions_loc);
    $assertions = substr($file, $assertions_loc + 12, $end_assertions_loc - $assertions_loc - 12);

    $failures_loc = strpos($file, "Errors: ", $assertions_loc);
    $end_failures_loc = strpos($file, ".", $failures_loc);
    $failures = trim(substr($file, $failures_loc + 7, $end_failures_loc - $failures_loc - 7));

    $output['results']['summary']['tests'] = (int)$assertions;
    $output['results']['summary']['passed'] = (int)$assertions - (int)$failures;
    $output['results']['summary']['failed'] = (int)$failures;
}
echo json_encode($output);
