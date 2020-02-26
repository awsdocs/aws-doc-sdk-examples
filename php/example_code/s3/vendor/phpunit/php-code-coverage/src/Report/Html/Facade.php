<?php declare(strict_types=1);
/*
 * This file is part of phpunit/php-code-coverage.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\CodeCoverage\Report\Html;

use SebastianBergmann\CodeCoverage\CodeCoverage;
use SebastianBergmann\CodeCoverage\Node\Directory as DirectoryNode;
use SebastianBergmann\CodeCoverage\RuntimeException;

/**
 * Generates an HTML report from a code coverage object.
 */
final class Facade
{
    /**
     * @var string
     */
    private $templatePath;

    /**
     * @var string
     */
    private $generator;

    /**
     * @var int
     */
    private $lowUpperBound;

    /**
     * @var int
     */
    private $highLowerBound;

    public function __construct(int $lowUpperBound = 50, int $highLowerBound = 90, string $generator = '')
    {
        $this->generator      = $generator;
        $this->highLowerBound = $highLowerBound;
        $this->lowUpperBound  = $lowUpperBound;
        $this->templatePath   = __DIR__ . '/Renderer/Template/';
    }

    /**
     * @throws RuntimeException
     * @throws \InvalidArgumentException
     * @throws \RuntimeException
     */
    public function process(CodeCoverage $coverage, string $target): void
    {
        $target = $this->getDirectory($target);
        $report = $coverage->getReport();

        if (!isset($_SERVER['REQUEST_TIME'])) {
            $_SERVER['REQUEST_TIME'] = \time();
        }

        $date = \date('D M j G:i:s T Y', $_SERVER['REQUEST_TIME']);

        $dashboard = new Dashboard(
            $this->templatePath,
            $this->generator,
            $date,
            $this->lowUpperBound,
            $this->highLowerBound
        );

        $directory = new Directory(
            $this->templatePath,
            $this->generator,
            $date,
            $this->lowUpperBound,
            $this->highLowerBound
        );

        $file = new File(
            $this->templatePath,
            $this->generator,
            $date,
            $this->lowUpperBound,
            $this->highLowerBound
        );

        $directory->render($report, $target . 'index.html');
        $dashboard->render($report, $target . 'dashboard.html');

        foreach ($report as $node) {
            $id = $node->getId();

            if ($node instanceof DirectoryNode) {
                if (!$this->createDirectory($target . $id)) {
                    throw new \RuntimeException(\sprintf('Directory "%s" was not created', $target . $id));
                }

                $directory->render($node, $target . $id . '/index.html');
                $dashboard->render($node, $target . $id . '/dashboard.html');
            } else {
                $dir = \dirname($target . $id);

                if (!$this->createDirectory($dir)) {
                    throw new \RuntimeException(\sprintf('Directory "%s" was not created', $dir));
                }

                $file->render($node, $target . $id . '.html');
            }
        }

        $this->copyFiles($target);
    }

    /**
     * @throws RuntimeException
     */
    private function copyFiles(string $target): void
    {
        $dir = $this->getDirectory($target . '_css');

        \copy($this->templatePath . 'css/bootstrap.min.css', $dir . 'bootstrap.min.css');
        \copy($this->templatePath . 'css/nv.d3.min.css', $dir . 'nv.d3.min.css');
        \copy($this->templatePath . 'css/style.css', $dir . 'style.css');
        \copy($this->templatePath . 'css/custom.css', $dir . 'custom.css');
        \copy($this->templatePath . 'css/octicons.css', $dir . 'octicons.css');

        $dir = $this->getDirectory($target . '_icons');
        \copy($this->templatePath . 'icons/file-code.svg', $dir . 'file-code.svg');
        \copy($this->templatePath . 'icons/file-directory.svg', $dir . 'file-directory.svg');

        $dir = $this->getDirectory($target . '_js');
        \copy($this->templatePath . 'js/bootstrap.min.js', $dir . 'bootstrap.min.js');
        \copy($this->templatePath . 'js/popper.min.js', $dir . 'popper.min.js');
        \copy($this->templatePath . 'js/d3.min.js', $dir . 'd3.min.js');
        \copy($this->templatePath . 'js/jquery.min.js', $dir . 'jquery.min.js');
        \copy($this->templatePath . 'js/nv.d3.min.js', $dir . 'nv.d3.min.js');
        \copy($this->templatePath . 'js/file.js', $dir . 'file.js');
    }

    /**
     * @throws RuntimeException
     */
    private function getDirectory(string $directory): string
    {
        if (\substr($directory, -1, 1) != \DIRECTORY_SEPARATOR) {
            $directory .= \DIRECTORY_SEPARATOR;
        }

        if (!$this->createDirectory($directory)) {
            throw new RuntimeException(
                \sprintf(
                    'Directory "%s" does not exist.',
                    $directory
                )
            );
        }

        return $directory;
    }

    private function createDirectory(string $directory): bool
    {
        return !(!\is_dir($directory) && !@\mkdir($directory, 0777, true) && !\is_dir($directory));
    }
}
