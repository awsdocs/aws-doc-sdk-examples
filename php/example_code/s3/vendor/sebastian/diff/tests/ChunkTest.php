<?php declare(strict_types=1);
/*
 * This file is part of sebastian/diff.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace SebastianBergmann\Diff;

use PHPUnit\Framework\TestCase;

/**
 * @covers SebastianBergmann\Diff\Chunk
 *
 * @uses SebastianBergmann\Diff\Line
 */
final class ChunkTest extends TestCase
{
    /**
     * @var Chunk
     */
    private $chunk;

    protected function setUp(): void
    {
        $this->chunk = new Chunk;
    }

    public function testHasInitiallyNoLines(): void
    {
        $this->assertSame([], $this->chunk->getLines());
    }

    public function testCanBeCreatedWithoutArguments(): void
    {
        $this->assertInstanceOf(Chunk::class, $this->chunk);
    }

    public function testStartCanBeRetrieved(): void
    {
        $this->assertSame(0, $this->chunk->getStart());
    }

    public function testStartRangeCanBeRetrieved(): void
    {
        $this->assertSame(1, $this->chunk->getStartRange());
    }

    public function testEndCanBeRetrieved(): void
    {
        $this->assertSame(0, $this->chunk->getEnd());
    }

    public function testEndRangeCanBeRetrieved(): void
    {
        $this->assertSame(1, $this->chunk->getEndRange());
    }

    public function testLinesCanBeRetrieved(): void
    {
        $this->assertSame([], $this->chunk->getLines());
    }

    public function testLinesCanBeSet(): void
    {
        $lines = [new Line(Line::ADDED, 'added'), new Line(Line::REMOVED, 'removed')];

        $this->chunk->setLines($lines);

        $this->assertSame($lines, $this->chunk->getLines());
    }
}
