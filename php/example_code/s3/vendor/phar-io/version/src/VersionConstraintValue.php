<?php

namespace PharIo\Version;

class VersionConstraintValue {
    /**
     * @var VersionNumber
     */
    private $major;

    /**
     * @var VersionNumber
     */
    private $minor;

    /**
     * @var VersionNumber
     */
    private $patch;

    /**
     * @var string
     */
    private $label = '';

    /**
     * @var string
     */
    private $buildMetaData = '';

    /**
     * @var string
     */
    private $versionString = '';

    /**
     * @param string $versionString
     */
    public function __construct($versionString) {
        $this->versionString = $versionString;

        $this->parseVersion($versionString);
    }

    /**
     * @return string
     */
    public function getLabel() {
        return $this->label;
    }

    /**
     * @return string
     */
    public function getBuildMetaData() {
        return $this->buildMetaData;
    }

    /**
     * @return string
     */
    public function getVersionString() {
        return $this->versionString;
    }

    /**
     * @return VersionNumber
     */
    public function getMajor() {
        return $this->major;
    }

    /**
     * @return VersionNumber
     */
    public function getMinor() {
        return $this->minor;
    }

    /**
     * @return VersionNumber
     */
    public function getPatch() {
        return $this->patch;
    }

    /**
     * @param $versionString
     */
    private function parseVersion($versionString) {
        $this->extractBuildMetaData($versionString);
        $this->extractLabel($versionString);

        $versionSegments = explode('.', $versionString);
        $this->major = new VersionNumber($versionSegments[0]);

        $minorValue = isset($versionSegments[1]) ? $versionSegments[1] : null;
        $patchValue = isset($versionSegments[2]) ? $versionSegments[2] : null;

        $this->minor = new VersionNumber($minorValue);
        $this->patch = new VersionNumber($patchValue);
    }

    /**
     * @param string $versionString
     */
    private function extractBuildMetaData(&$versionString) {
        if (preg_match('/\+(.*)/', $versionString, $matches) == 1) {
            $this->buildMetaData = $matches[1];
            $versionString = str_replace($matches[0], '', $versionString);
        }
    }

    /**
     * @param string $versionString
     */
    private function extractLabel(&$versionString) {
        if (preg_match('/\-(.*)/', $versionString, $matches) == 1) {
            $this->label = $matches[1];
            $versionString = str_replace($matches[0], '', $versionString);
        }
    }
}
