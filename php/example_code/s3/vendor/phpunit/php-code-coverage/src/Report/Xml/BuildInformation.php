<?php declare(strict_types=1);
/*
 * This file is part of phpunit/php-code-coverage.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
namespace SebastianBergmann\CodeCoverage\Report\Xml;

use SebastianBergmann\Environment\Runtime;

final class BuildInformation
{
    /**
     * @var \DOMElement
     */
    private $contextNode;

    public function __construct(\DOMElement $contextNode)
    {
        $this->contextNode = $contextNode;
    }

    public function setRuntimeInformation(Runtime $runtime): void
    {
        $runtimeNode = $this->getNodeByName('runtime');

        $runtimeNode->setAttribute('name', $runtime->getName());
        $runtimeNode->setAttribute('version', $runtime->getVersion());
        $runtimeNode->setAttribute('url', $runtime->getVendorUrl());

        $driverNode = $this->getNodeByName('driver');

        if ($runtime->hasPHPDBGCodeCoverage()) {
            $driverNode->setAttribute('name', 'phpdbg');
            $driverNode->setAttribute('version', \constant('PHPDBG_VERSION'));
        }

        if ($runtime->hasXdebug()) {
            $driverNode->setAttribute('name', 'xdebug');
            $driverNode->setAttribute('version', \phpversion('xdebug'));
        }

        if ($runtime->hasPCOV()) {
            $driverNode->setAttribute('name', 'pcov');
            $driverNode->setAttribute('version', \phpversion('pcov'));
        }
    }

    public function setBuildTime(\DateTime $date): void
    {
        $this->contextNode->setAttribute('time', $date->format('D M j G:i:s T Y'));
    }

    public function setGeneratorVersions(string $phpUnitVersion, string $coverageVersion): void
    {
        $this->contextNode->setAttribute('phpunit', $phpUnitVersion);
        $this->contextNode->setAttribute('coverage', $coverageVersion);
    }

    private function getNodeByName(string $name): \DOMElement
    {
        $node = $this->contextNode->getElementsByTagNameNS(
            'https://schema.phpunit.de/coverage/1.0',
            $name
        )->item(0);

        if (!$node) {
            $node = $this->contextNode->appendChild(
                $this->contextNode->ownerDocument->createElementNS(
                    'https://schema.phpunit.de/coverage/1.0',
                    $name
                )
            );
        }

        return $node;
    }
}
