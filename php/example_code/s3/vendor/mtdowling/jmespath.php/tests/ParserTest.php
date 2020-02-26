<?php
namespace JmesPath\Tests;

use JmesPath\Lexer;
use JmesPath\Parser;
use PHPUnit\Framework\TestCase;

/**
 * @covers JmesPath\Parser
 */
class ParserTest extends TestCase
{
    /**
     * @expectedException \JmesPath\SyntaxErrorException
     * @expectedExceptionMessage Syntax error at character 0
     */
    public function testMatchesFirstTokens()
    {
        $p = new Parser(new Lexer());
        $p->parse('.bar');
    }

    /**
     * @expectedException \JmesPath\SyntaxErrorException
     * @expectedExceptionMessage Syntax error at character 1
     */
    public function testThrowsSyntaxErrorForInvalidSequence()
    {
        $p = new Parser(new Lexer());
        $p->parse('a,');
    }

    /**
     * @expectedException \JmesPath\SyntaxErrorException
     * @expectedExceptionMessage Syntax error at character 2
     */
    public function testMatchesAfterFirstToken()
    {
        $p = new Parser(new Lexer());
        $p->parse('a.,');
    }

    /**
     * @expectedException \JmesPath\SyntaxErrorException
     * @expectedExceptionMessage Unexpected "eof" token
     */
    public function testHandlesEmptyExpressions()
    {
        (new Parser(new Lexer()))->parse('');
    }
}
