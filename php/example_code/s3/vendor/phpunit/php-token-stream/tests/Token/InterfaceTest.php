<?php
/*
 * This file is part of php-token-stream.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

use PHPUnit\Framework\TestCase;

class PHP_Token_InterfaceTest extends TestCase
{
    /**
     * @var PHP_Token_CLASS
     */
    private $class;

    /**
     * @var PHP_Token_INTERFACE[]
     */
    private $interfaces;

    protected function setUp(): void
    {
        $ts = new PHP_Token_Stream(TEST_FILES_PATH . 'source4.php');
        $i  = 0;

        foreach ($ts as $token) {
            if ($token instanceof PHP_Token_CLASS) {
                $this->class = $token;
            } elseif ($token instanceof PHP_Token_INTERFACE) {
                $this->interfaces[$i] = $token;
                $i++;
            }
        }
    }

    public function testGetName()
    {
        $this->assertEquals(
            'iTemplate', $this->interfaces[0]->getName()
        );
    }

    public function testGetParentNotExists()
    {
        $this->assertFalse(
            $this->interfaces[0]->getParent()
        );
    }

    public function testHasParentNotExists()
    {
        $this->assertFalse(
            $this->interfaces[0]->hasParent()
        );
    }

    public function testGetParentExists()
    {
        $this->assertEquals(
            'a', $this->interfaces[2]->getParent()
        );
    }

    public function testHasParentExists()
    {
        $this->assertTrue(
            $this->interfaces[2]->hasParent()
        );
    }

    public function testGetInterfacesExists()
    {
        $this->assertEquals(
            ['b'],
            $this->class->getInterfaces()
        );
    }

    public function testHasInterfacesExists()
    {
        $this->assertTrue(
            $this->class->hasInterfaces()
        );
    }

    public function testGetPackageNamespace()
    {
        foreach (new PHP_Token_Stream(TEST_FILES_PATH . 'classInNamespace.php') as $token) {
            if ($token instanceof PHP_Token_INTERFACE) {
                $package = $token->getPackage();
                $this->assertSame('Foo\\Bar', $package['namespace']);
            }
        }
    }

    public function provideFilesWithClassesWithinMultipleNamespaces()
    {
        return [
            [TEST_FILES_PATH . 'multipleNamespacesWithOneClassUsingBraces.php'],
            [TEST_FILES_PATH . 'multipleNamespacesWithOneClassUsingNonBraceSyntax.php'],
        ];
    }

    /**
     * @dataProvider provideFilesWithClassesWithinMultipleNamespaces
     */
    public function testGetPackageNamespaceForFileWithMultipleNamespaces($filepath)
    {
        $tokenStream     = new PHP_Token_Stream($filepath);
        $firstClassFound = false;

        foreach ($tokenStream as $token) {
            if ($firstClassFound === false && $token instanceof PHP_Token_INTERFACE) {
                $package = $token->getPackage();
                $this->assertSame('TestClassInBar', $token->getName());
                $this->assertSame('Foo\\Bar', $package['namespace']);
                $firstClassFound = true;
                continue;
            }
            // Secound class
            if ($token instanceof PHP_Token_INTERFACE) {
                $package = $token->getPackage();
                $this->assertSame('TestClassInBaz', $token->getName());
                $this->assertSame('Foo\\Baz', $package['namespace']);

                return;
            }
        }
        $this->fail('Searching for 2 classes failed');
    }

    public function testGetPackageNamespaceIsEmptyForInterfacesThatAreNotWithinNamespaces()
    {
        foreach ($this->interfaces as $token) {
            $package = $token->getPackage();
            $this->assertSame('', $package['namespace']);
        }
    }

    public function testGetPackageNamespaceWhenExtentingFromNamespaceClass()
    {
        $tokenStream     = new PHP_Token_Stream(TEST_FILES_PATH . 'classExtendsNamespacedClass.php');
        $firstClassFound = false;

        foreach ($tokenStream as $token) {
            if ($firstClassFound === false && $token instanceof PHP_Token_INTERFACE) {
                $package = $token->getPackage();
                $this->assertSame('Baz', $token->getName());
                $this->assertSame('Foo\\Bar', $package['namespace']);
                $firstClassFound = true;
                continue;
            }

            if ($token instanceof PHP_Token_INTERFACE) {
                $package = $token->getPackage();
                $this->assertSame('Extender', $token->getName());
                $this->assertSame('Other\\Space', $package['namespace']);

                return;
            }
        }

        $this->fail('Searching for 2 classes failed');
    }
}
