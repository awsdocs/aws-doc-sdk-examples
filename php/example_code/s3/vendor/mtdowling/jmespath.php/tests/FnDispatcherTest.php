<?php
namespace JmesPath\Tests;

use JmesPath\fnDispatcher;
use PHPUnit\Framework\TestCase;

class fnDispatcherTest extends TestCase
{
    public function testConvertsToString()
    {
        $fn = new FnDispatcher();
        $this->assertEquals('foo', $fn('to_string', ['foo']));
        $this->assertEquals('1', $fn('to_string', [1]));
        $this->assertEquals('["foo"]', $fn('to_string', [['foo']]));
        $std = new \stdClass();
        $std->foo = 'bar';
        $this->assertEquals('{"foo":"bar"}', $fn('to_string', [$std]));
        $this->assertEquals('foo', $fn('to_string', [new _TestStringClass()]));
        $this->assertEquals('"foo"', $fn('to_string', [new _TestJsonStringClass()]));
    }
}

class _TestStringClass
{
    public function __toString()
    {
        return 'foo';
    }
}

class _TestJsonStringClass implements \JsonSerializable
{
    public function __toString()
    {
        return 'no!';
    }

    public function jsonSerialize()
    {
        return 'foo';
    }
}
