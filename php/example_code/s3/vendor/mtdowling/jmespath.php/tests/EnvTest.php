<?php
namespace JmesPath\Tests;

use JmesPath\Env;
use JmesPath\CompilerRuntime;
use PHPUnit\Framework\TestCase;

class EnvTest extends TestCase
{
    public function testSearchesInput()
    {
        $data = ['foo' => 123];
        $this->assertEquals(123, Env::search('foo', $data));
        $this->assertEquals(123, Env::search('foo', $data));
    }

    public function testSearchesWithFunction()
    {
        $data = ['foo' => 123];
        $this->assertEquals(123, \JmesPath\search('foo', $data));
    }

    public function testCleansCompileDir()
    {
        $dir = sys_get_temp_dir();
        $runtime = new CompilerRuntime($dir);
        $runtime('@ | @ | @[0][0][0]', []);
        $this->assertNotEmpty(glob($dir . '/jmespath_*.php'));
        $this->assertGreaterThan(0, Env::cleanCompileDir());
        $this->assertEmpty(glob($dir . '/jmespath_*.php'));
    }
}
