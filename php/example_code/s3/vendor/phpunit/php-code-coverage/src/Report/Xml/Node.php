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

abstract class Node
{
    /**
     * @var \DOMDocument
     */
    private $dom;

    /**
     * @var \DOMElement
     */
    private $contextNode;

    public function __construct(\DOMElement $context)
    {
        $this->setContextNode($context);
    }

    public function getDom(): \DOMDocument
    {
        return $this->dom;
    }

    public function getTotals(): Totals
    {
        $totalsContainer = $this->getContextNode()->firstChild;

        if (!$totalsContainer) {
            $totalsContainer = $this->getContextNode()->appendChild(
                $this->dom->createElementNS(
                    'https://schema.phpunit.de/coverage/1.0',
                    'totals'
                )
            );
        }

        return new Totals($totalsContainer);
    }

    public function addDirectory(string $name): Directory
    {
        $dirNode = $this->getDom()->createElementNS(
            'https://schema.phpunit.de/coverage/1.0',
            'directory'
        );

        $dirNode->setAttribute('name', $name);
        $this->getContextNode()->appendChild($dirNode);

        return new Directory($dirNode);
    }

    public function addFile(string $name, string $href): File
    {
        $fileNode = $this->getDom()->createElementNS(
            'https://schema.phpunit.de/coverage/1.0',
            'file'
        );

        $fileNode->setAttribute('name', $name);
        $fileNode->setAttribute('href', $href);
        $this->getContextNode()->appendChild($fileNode);

        return new File($fileNode);
    }

    protected function setContextNode(\DOMElement $context): void
    {
        $this->dom         = $context->ownerDocument;
        $this->contextNode = $context;
    }

    protected function getContextNode(): \DOMElement
    {
        return $this->contextNode;
    }
}
