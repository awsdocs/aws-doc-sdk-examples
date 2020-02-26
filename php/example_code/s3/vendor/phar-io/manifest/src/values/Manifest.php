<?php
/*
 * This file is part of PharIo\Manifest.
 *
 * (c) Arne Blankerts <arne@blankerts.de>, Sebastian Heuer <sebastian@phpeople.de>, Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace PharIo\Manifest;

use PharIo\Version\Version;

class Manifest {
    /**
     * @var ApplicationName
     */
    private $name;

    /**
     * @var Version
     */
    private $version;

    /**
     * @var Type
     */
    private $type;

    /**
     * @var CopyrightInformation
     */
    private $copyrightInformation;

    /**
     * @var RequirementCollection
     */
    private $requirements;

    /**
     * @var BundledComponentCollection
     */
    private $bundledComponents;

    public function __construct(ApplicationName $name, Version $version, Type $type, CopyrightInformation $copyrightInformation, RequirementCollection $requirements, BundledComponentCollection $bundledComponents) {
        $this->name                 = $name;
        $this->version              = $version;
        $this->type                 = $type;
        $this->copyrightInformation = $copyrightInformation;
        $this->requirements         = $requirements;
        $this->bundledComponents    = $bundledComponents;
    }

    /**
     * @return ApplicationName
     */
    public function getName() {
        return $this->name;
    }

    /**
     * @return Version
     */
    public function getVersion() {
        return $this->version;
    }

    /**
     * @return Type
     */
    public function getType() {
        return $this->type;
    }

    /**
     * @return CopyrightInformation
     */
    public function getCopyrightInformation() {
        return $this->copyrightInformation;
    }

    /**
     * @return RequirementCollection
     */
    public function getRequirements() {
        return $this->requirements;
    }

    /**
     * @return BundledComponentCollection
     */
    public function getBundledComponents() {
        return $this->bundledComponents;
    }

    /**
     * @return bool
     */
    public function isApplication() {
        return $this->type->isApplication();
    }

    /**
     * @return bool
     */
    public function isLibrary() {
        return $this->type->isLibrary();
    }

    /**
     * @return bool
     */
    public function isExtension() {
        return $this->type->isExtension();
    }

    /**
     * @param ApplicationName $application
     * @param Version|null    $version
     *
     * @return bool
     */
    public function isExtensionFor(ApplicationName $application, Version $version = null) {
        if (!$this->isExtension()) {
            return false;
        }

        /** @var Extension $type */
        $type = $this->type;

        if ($version !== null) {
            return $type->isCompatibleWith($application, $version);
        }

        return $type->isExtensionFor($application);
    }
}
