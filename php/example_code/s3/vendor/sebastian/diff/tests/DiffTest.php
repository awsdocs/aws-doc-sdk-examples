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
 * @covers SebastianBergmann\Diff\Diff
 *
 * @uses SebastianBergmann\Diff\Chunk
 */
final class DiffTest extends TestCase
{
    public function testGettersAfterConstructionWithDefault(): void
    {
        $from = 'line1a';
        $to   = 'line2a';
        $diff = new Diff($from, $to);

        $this->assertSame($from, $diff->getFrom());
        $this->assertSame($to, $diff->getTo());
        $this->assertSame([], $diff->getChunks(), 'Expect chunks to be default value "array()".');
    }

    public function testGettersAfterConstructionWithChunks(): void
    {
        $from   = 'line1b';
        $to     = 'line2b';
        $chunks = [new Chunk(), new Chunk(2, 3)];

        $diff = new Diff($from, $to, $chunks);

        $this->assertSame($from, $diff->getFrom());
        $this->assertSame($to, $diff->getTo());
        $this->assertSame($chunks, $diff->getChunks(), 'Expect chunks to be passed value.');
    }

    public function testSetChunksAfterConstruction(): void
    {
        $diff = new Diff('line1c', 'line2c');
        $this->assertSame([], $diff->getChunks(), 'Expect chunks to be default value "array()".');

        $chunks = [new Chunk(), new Chunk(2, 3)];
        $diff->setChunks($chunks);
        $this->assertSame($chunks, $diff->getChunks(), 'Expect chunks to be passed value.');
    }
}
