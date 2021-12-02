# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with PyTest and the AWS Device Farm
browser testing feature.
"""

# snippet-start:[python.example_code.device-farm.Scenario_BrowserTesting]
import datetime
import os
import subprocess
import boto3
import pytest
from selenium import webdriver
from selenium.webdriver import DesiredCapabilities
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support import expected_conditions
from selenium.webdriver.support.wait import WebDriverWait


def get_git_hash():
    """
    Get the short Git hash of the current commit of the repository
    """
    try:
        return subprocess.check_output(
            ['git', 'rev-parse', '--short', 'HEAD']).decode('utf-8').strip()
    except:
        return "norepo"


class TestHelloSuite:
    """
    Our test suite.

    This style of test suite allows us to use setup_method and teardown_method.

    """
    def save_screenshot(self, name):
        self.driver.save_screenshot(os.path.join(self.screenshot_path, name))

    def setup_method(self, method):
        """
        Set up a test.

        This makes sure that the session for an individual test is ready.

        The AWS credentials are read from the default ~/.aws/credentials or from the
        command line by setting the AWS_ACCESS_KEY_ID and AWS_SECRET_KEY environment
        variables.

        The project Amazon Resource Name (ARN) is determined by the PROJECT_ARN
        environment variable.
        """
        devicefarm_client = boto3.client("devicefarm")
        project_arn = os.environ.get("PROJECT_ARN", None)
        if project_arn is None:
            raise ValueError("Must set PROJECT_ARN")
        # Request a driver hub URL for the Selenium client
        testgrid_url_response = devicefarm_client.create_test_grid_url(
            projectArn=project_arn,
            expiresInSeconds=300)

        # We want a directory to save our files into. We're going to make a directory
        # in the current directory that holds our results.
        self.screenshot_path = os.path.join(
            '.', 'results', get_git_hash() + '-' + (datetime.date.today().isoformat()))
        if not os.path.exists(self.screenshot_path):
            os.makedirs(self.screenshot_path, exist_ok=True)

        # We want a Firefox instance on Windows
        desired_cap = DesiredCapabilities.FIREFOX
        desired_cap['platform'] = 'windows'
        desired_cap['BrowserVersion'] = 'latest'

        # Configure the webdriver with the appropriate remote endpoint.
        self.driver = webdriver.Remote(testgrid_url_response['url'], desired_cap)

        #
        # Auto-Tagging
        #

        # In order to get the Session ARN, we need to look up the session by the
        # Project ARN and session ID (from the driver).
        testgrid_session_arn_response = devicefarm_client.get_test_grid_session(
            projectArn=project_arn,
            sessionId=self.driver.session_id
        )

        # Save the session's ARN so we can tag the session.
        self.session_arn = testgrid_session_arn_response['testGridSession']['arn']

        # In order to tag it, we're going to use the resourcegroupstaggingapi client to
        # add a tag to the session ARN that we just got.
        tag_client = boto3.client('resourcegroupstaggingapi')
        tag_client.tag_resources(
            ResourceARNList=[self.session_arn],
            Tags={
                "TestSuite": f"testsuite {method.__name__}",
                "GitId": get_git_hash()})

    def teardown_method(self, method):
        """
        Clean up resources used by each method.
        """
        # End the Selenium session so we're off the clock.
        self.driver.quit()

    @pytest.mark.parametrize(
        'query,leading', [
            pytest.param('Seattle', 'Seattle (/siˈætəl/ (listen) see-AT-əl) is a seaport city on the West Coast of the United States.'),
            pytest.param('Selenium', "Selenium is a chemical element with the symbol Se and atomic number 34."),
            pytest.param('Amazon Locker', 'Amazon Locker is a self-service package delivery service offered by online retailer Amazon.'),
            pytest.param('Kootenai Falls','Kootenai Falls is a waterfall on the Kootenay River located in Lincoln County, Montana, just off U.S. Route 2.'),
            pytest.param('Dorayaki', 'Dorayaki (どら焼き, どらやき, 銅鑼焼き, ドラ焼き) is a type of Japanese confection.'),
            pytest.param('Robot Face', '<|°_°|> (also known as Robot Face or Robot)')
        ])
    def test_first_paragraph_text(self, query, leading):
        """
        This test looks at the first paragraph of a page on Wikipedia, comparing it to
        a known leading sentence.

        If the leading sentence matches, the test passes. A screenshot is taken before
        the final assertion is made, letting us debug if something isn't right.
        """
        # Open the main page of Wikipedia
        self.driver.get("https://en.wikipedia.org/wiki/Main_Page")
        # Find the search box, enter a query, and press enter
        search_input = self.driver.find_element(By.ID, "searchInput")
        search_input.click()
        search_input.send_keys(query)
        search_input.send_keys(Keys.ENTER)
        # Wait for the search box to go stale -- This means we've navigated fully.
        WebDriverWait(self.driver, 5).until(expected_conditions.staleness_of(search_input))
        # Get the leading paragraph of the article.
        lead = leading.lower()
        # Find the element...
        lead_para = self.driver.find_element(
            By.XPATH, "//div[@class='mw-parser-output']//p[not(@class)]")
        # ... and copy out its text.
        our_text = lead_para.text.lower()
        our_text = our_text[:len(lead)]
        # Take a screenshot and compare the strings.
        self.save_screenshot(f"leadingpara_{query}.png")
        assert our_text.startswith(lead)

    @pytest.mark.parametrize(
        "query,expected", [
            pytest.param("Automation Testing", "Test Automation"),
            pytest.param("DevOps", "DevOps"),
            pytest.param("Jackdaws Love My Big Sphinx Of Quartz", "Pangram"),
            pytest.param("EarthBound", "EarthBound"),
            pytest.param("Covered Bridges Today", "Covered Bridges Today"),
            pytest.param("Kurt Godel", "Kurt Gödel"),
            pytest.param("N//ng language", "Nǁng language"),
            pytest.param("Who the Fuck Is Jackson Pollock?", "Who the $&% Is Jackson Pollock?")
        ])
    def test_redirect_titles(self, query, expected):
        """
        A test comparing pages we expect to (or not to) redirect on Wikipedia.

        This test checks to see that the page ("query") redirects (or doesn't) to the
        "expected" page title. Several of these are common synonyms ("Jackdaws...")
        while others are because of characters untypable by most keyboards ("Nǁng language")

        A screenshot is taken just before the final assertion is made to aid in
        debugging and verification.
        """
        # Open the main page of Wikipedia
        self.driver.get("https://en.wikipedia.org/wiki/Main_Page")
        # Find the search box, enter some text into it, and send an enter key.
        search_input = self.driver.find_element(By.ID, "searchInput")
        search_input.click()
        search_input.send_keys(query)
        search_input.send_keys(Keys.ENTER)
        # wait until the page has rolled over -- once the search input handle is stale,
        # the browser has navigated.
        WebDriverWait(self.driver, 5).until(expected_conditions.staleness_of(search_input))
        # Get the first heading & take a screenshot
        our_text = self.driver.find_element(By.ID, "firstHeading").text.lower()
        self.save_screenshot(f"redirect_{query}.png")
        # did it match?
        assert our_text == expected.lower()
# snippet-end:[python.example_code.device-farm.Scenario_BrowserTesting]
