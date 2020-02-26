<?php declare(strict_types=1);
/*
 * This file is part of phpunit/php-code-coverage.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\CodeCoverage\Report;

use SebastianBergmann\CodeCoverage\CodeCoverage;
use SebastianBergmann\CodeCoverage\Node\File;
use SebastianBergmann\CodeCoverage\RuntimeException;

/**
 * Generates a Clover XML logfile from a code coverage object.
 */
final class Clover
{
    /**
     * @throws \RuntimeException
     */
    public function process(CodeCoverage $coverage, ?string $target = null, ?string $name = null): string
    {
        $xmlDocument               = new \DOMDocument('1.0', 'UTF-8');
        $xmlDocument->formatOutput = true;

        $xmlCoverage = $xmlDocument->createElement('coverage');
        $xmlCoverage->setAttribute('generated', (string) $_SERVER['REQUEST_TIME']);
        $xmlDocument->appendChild($xmlCoverage);

        $xmlProject = $xmlDocument->createElement('project');
        $xmlProject->setAttribute('timestamp', (string) $_SERVER['REQUEST_TIME']);

        if (\is_string($name)) {
            $xmlProject->setAttribute('name', $name);
        }

        $xmlCoverage->appendChild($xmlProject);

        $packages = [];
        $report   = $coverage->getReport();

        foreach ($report as $item) {
            if (!$item instanceof File) {
                continue;
            }

            /* @var File $item */

            $xmlFile = $xmlDocument->createElement('file');
            $xmlFile->setAttribute('name', $item->getPath());

            $classes      = $item->getClassesAndTraits();
            $coverageData = $item->getCoverageData();
            $lines        = [];
            $namespace    = 'global';

            foreach ($classes as $className => $class) {
                $classStatements        = 0;
                $coveredClassStatements = 0;
                $coveredMethods         = 0;
                $classMethods           = 0;

                foreach ($class['methods'] as $methodName => $method) {
                    if ($method['executableLines'] == 0) {
                        continue;
                    }

                    $classMethods++;
                    $classStatements += $method['executableLines'];
                    $coveredClassStatements += $method['executedLines'];

                    if ($method['coverage'] == 100) {
                        $coveredMethods++;
                    }

                    $methodCount = 0;

                    foreach (\range($method['startLine'], $method['endLine']) as $line) {
                        if (isset($coverageData[$line]) && ($coverageData[$line] !== null)) {
                            $methodCount = \max($methodCount, \count($coverageData[$line]));
                        }
                    }

                    $lines[$method['startLine']] = [
                        'ccn'         => $method['ccn'],
                        'count'       => $methodCount,
                        'crap'        => $method['crap'],
                        'type'        => 'method',
                        'visibility'  => $method['visibility'],
                        'name'        => $methodName,
                    ];
                }

                if (!empty($class['package']['namespace'])) {
                    $namespace = $class['package']['namespace'];
                }

                $xmlClass = $xmlDocument->createElement('class');
                $xmlClass->setAttribute('name', $className);
                $xmlClass->setAttribute('namespace', $namespace);

                if (!empty($class['package']['fullPackage'])) {
                    $xmlClass->setAttribute(
                        'fullPackage',
                        $class['package']['fullPackage']
                    );
                }

                if (!empty($class['package']['category'])) {
                    $xmlClass->setAttribute(
                        'category',
                        $class['package']['category']
                    );
                }

                if (!empty($class['package']['package'])) {
                    $xmlClass->setAttribute(
                        'package',
                        $class['package']['package']
                    );
                }

                if (!empty($class['package']['subpackage'])) {
                    $xmlClass->setAttribute(
                        'subpackage',
                        $class['package']['subpackage']
                    );
                }

                $xmlFile->appendChild($xmlClass);

                $xmlMetrics = $xmlDocument->createElement('metrics');
                $xmlMetrics->setAttribute('complexity', (string) $class['ccn']);
                $xmlMetrics->setAttribute('methods', (string) $classMethods);
                $xmlMetrics->setAttribute('coveredmethods', (string) $coveredMethods);
                $xmlMetrics->setAttribute('conditionals', '0');
                $xmlMetrics->setAttribute('coveredconditionals', '0');
                $xmlMetrics->setAttribute('statements', (string) $classStatements);
                $xmlMetrics->setAttribute('coveredstatements', (string) $coveredClassStatements);
                $xmlMetrics->setAttribute('elements', (string) ($classMethods + $classStatements /* + conditionals */));
                $xmlMetrics->setAttribute('coveredelements', (string) ($coveredMethods + $coveredClassStatements /* + coveredconditionals */));
                $xmlClass->appendChild($xmlMetrics);
            }

            foreach ($coverageData as $line => $data) {
                if ($data === null || isset($lines[$line])) {
                    continue;
                }

                $lines[$line] = [
                    'count' => \count($data), 'type' => 'stmt',
                ];
            }

            \ksort($lines);

            foreach ($lines as $line => $data) {
                $xmlLine = $xmlDocument->createElement('line');
                $xmlLine->setAttribute('num', (string) $line);
                $xmlLine->setAttribute('type', $data['type']);

                if (isset($data['name'])) {
                    $xmlLine->setAttribute('name', $data['name']);
                }

                if (isset($data['visibility'])) {
                    $xmlLine->setAttribute('visibility', $data['visibility']);
                }

                if (isset($data['ccn'])) {
                    $xmlLine->setAttribute('complexity', (string) $data['ccn']);
                }

                if (isset($data['crap'])) {
                    $xmlLine->setAttribute('crap', (string) $data['crap']);
                }

                $xmlLine->setAttribute('count', (string) $data['count']);
                $xmlFile->appendChild($xmlLine);
            }

            $linesOfCode = $item->getLinesOfCode();

            $xmlMetrics = $xmlDocument->createElement('metrics');
            $xmlMetrics->setAttribute('loc', (string) $linesOfCode['loc']);
            $xmlMetrics->setAttribute('ncloc', (string) $linesOfCode['ncloc']);
            $xmlMetrics->setAttribute('classes', (string) $item->getNumClassesAndTraits());
            $xmlMetrics->setAttribute('methods', (string) $item->getNumMethods());
            $xmlMetrics->setAttribute('coveredmethods', (string) $item->getNumTestedMethods());
            $xmlMetrics->setAttribute('conditionals', '0');
            $xmlMetrics->setAttribute('coveredconditionals', '0');
            $xmlMetrics->setAttribute('statements', (string) $item->getNumExecutableLines());
            $xmlMetrics->setAttribute('coveredstatements', (string) $item->getNumExecutedLines());
            $xmlMetrics->setAttribute('elements', (string) ($item->getNumMethods() + $item->getNumExecutableLines() /* + conditionals */));
            $xmlMetrics->setAttribute('coveredelements', (string) ($item->getNumTestedMethods() + $item->getNumExecutedLines() /* + coveredconditionals */));
            $xmlFile->appendChild($xmlMetrics);

            if ($namespace === 'global') {
                $xmlProject->appendChild($xmlFile);
            } else {
                if (!isset($packages[$namespace])) {
                    $packages[$namespace] = $xmlDocument->createElement(
                        'package'
                    );

                    $packages[$namespace]->setAttribute('name', $namespace);
                    $xmlProject->appendChild($packages[$namespace]);
                }

                $packages[$namespace]->appendChild($xmlFile);
            }
        }

        $linesOfCode = $report->getLinesOfCode();

        $xmlMetrics = $xmlDocument->createElement('metrics');
        $xmlMetrics->setAttribute('files', (string) \count($report));
        $xmlMetrics->setAttribute('loc', (string) $linesOfCode['loc']);
        $xmlMetrics->setAttribute('ncloc', (string) $linesOfCode['ncloc']);
        $xmlMetrics->setAttribute('classes', (string) $report->getNumClassesAndTraits());
        $xmlMetrics->setAttribute('methods', (string) $report->getNumMethods());
        $xmlMetrics->setAttribute('coveredmethods', (string) $report->getNumTestedMethods());
        $xmlMetrics->setAttribute('conditionals', '0');
        $xmlMetrics->setAttribute('coveredconditionals', '0');
        $xmlMetrics->setAttribute('statements', (string) $report->getNumExecutableLines());
        $xmlMetrics->setAttribute('coveredstatements', (string) $report->getNumExecutedLines());
        $xmlMetrics->setAttribute('elements', (string) ($report->getNumMethods() + $report->getNumExecutableLines() /* + conditionals */));
        $xmlMetrics->setAttribute('coveredelements', (string) ($report->getNumTestedMethods() + $report->getNumExecutedLines() /* + coveredconditionals */));
        $xmlProject->appendChild($xmlMetrics);

        $buffer = $xmlDocument->saveXML();

        if ($target !== null) {
            if (!$this->createDirectory(\dirname($target))) {
                throw new \RuntimeException(\sprintf('Directory "%s" was not created', \dirname($target)));
            }

            if (@\file_put_contents($target, $buffer) === false) {
                throw new RuntimeException(
                    \sprintf(
                        'Could not write to "%s',
                        $target
                    )
                );
            }
        }

        return $buffer;
    }

    private function createDirectory(string $directory): bool
    {
        return !(!\is_dir($directory) && !@\mkdir($directory, 0777, true) && !\is_dir($directory));
    }
}
