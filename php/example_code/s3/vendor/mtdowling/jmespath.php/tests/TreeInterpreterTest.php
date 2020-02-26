<?php
namespace JmesPath\Tests\Tree;

use JmesPath\AstRuntime;
use JmesPath\TreeInterpreter;
use PHPUnit\Framework\TestCase;

/**
 * @covers JmesPath\Tree\TreeInterpreter
 */
class TreeInterpreterTest extends TestCase
{
    public function testReturnsNullWhenMergingNonArray()
    {
        $t = new TreeInterpreter();
        $this->assertNull($t->visit([
            'type' => 'flatten',
            'children' => [
                ['type' => 'literal', 'value' => 1],
                ['type' => 'literal', 'value' => 1]
            ]
        ], [], [
            'runtime' => new AstRuntime()
        ]));
    }

    public function testWorksWithArrayObjectAsObject()
    {
        $runtime = new AstRuntime();
        $this->assertEquals('baz', $runtime('foo.bar', new \ArrayObject([
            'foo' => new \ArrayObject(['bar' => 'baz'])
        ])));
    }

    public function testWorksWithArrayObjectAsArray()
    {
        $runtime = new AstRuntime();
        $this->assertEquals('baz', $runtime('foo[0].bar', new \ArrayObject([
            'foo' => new \ArrayObject([new \ArrayObject(['bar' => 'baz'])])
        ])));
    }

    public function testWorksWithArrayProjections()
    {
        $runtime = new AstRuntime();
        $this->assertEquals(
            ['baz'],
            $runtime('foo[*].bar', new \ArrayObject([
                'foo' => new \ArrayObject([
                    new \ArrayObject([
                        'bar' => 'baz'
                    ])
                ])
            ]))
        );
    }

    public function testWorksWithObjectProjections()
    {
        $runtime = new AstRuntime();
        $this->assertEquals(
            ['baz'],
            $runtime('foo.*.bar', new \ArrayObject([
                'foo' => new \ArrayObject([
                    'abc' => new \ArrayObject([
                        'bar' => 'baz'
                    ])
                ])
            ]))
        );
    }
}
