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

use SebastianBergmann\CodeCoverage\RuntimeException;

final class Coverage
{
    /**
     * @var \XMLWriter
     */
    private $writer;

    /**
     * @var \DOMElement
     */
    private $contextNode;

    /**
     * @var bool
     */
    private $finalized = false;

    public function __construct(\DOMElement $context, string $line)
    {
        $this->contextNode = $context;

        $this->writer = new \XMLWriter();
        $this->writer->openMemory();
        $this->writer->startElementNS(null, $context->nodeName, 'https://schema.phpunit.de/coverage/1.0');
        $this->writer->writeAttribute('nr', $line);
    }

    /**
     * @throws RuntimeException
     */
    public function addTest(string $test): void
    {
        if ($this->finalized) {
            throw new RuntimeException('Coverage Report already finalized');
        }

        $this->writer->startElement('covered');
        $this->writer->writeAttribute('by', $test);
        $this->writer->endElement();
    }

    public function finalize(): void
    {
        $this->writer->endElement();

        $fragment = $this->contextNode->ownerDocument->createDocumentFragment();
        $fragment->appendXML($this->writer->outputMemory());

        $this->contextNode->parentNode->replaceChild(
            $fragment,
            $this->contextNode
        );

        $this->finalized = true;
    }
}
