/*
 * (C) Copyright 2018 Boni Garcia (http://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.bonigarcia.webrtc.base;

import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_META;
import static java.awt.event.KeyEvent.VK_T;
import static java.lang.Thread.sleep;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfWindowsToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

public class WebRtcBase {

    final Logger log = getLogger(lookup().lookupClass());

    public void waitSeconds(int seconds) throws InterruptedException {
        sleep(SECONDS.toMillis(seconds));
    }

    public void openWebRtcInternalsInNewTab(WebDriver driver)
            throws AWTException {
        String webRtcInternalsUrl = "chrome://webrtc-internals/";
        log.debug("Opening {} in new tab", webRtcInternalsUrl);

        openNewTab(driver, webRtcInternalsUrl);
    }

    public void openNewTab(WebDriver driver, String url) throws AWTException {
        // Send control-t to the GUI
        sendControlT();

        // Wait to the new tab to be opened
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(numberOfWindowsToBe(2));

        // Switch to new tab
        ArrayList<String> list = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(list.get(1));

        // Open other URL in second tab
        driver.get(url);

        // Back to initial tab
        driver.switchTo().window(list.get(0));
    }

    public void downloadStats(WebDriver driver) throws InterruptedException {
        // Switch to webrtc-internal tab
        ArrayList<String> list = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(list.get(1));

        // Download
        log.debug("Downloading WebRTC stats");
        driver.findElement(By.cssSelector("#content-root > details > summary"))
                .click();
        driver.findElement(By.cssSelector(
                "#content-root > details > div > div:nth-child(1) > a > button"))
                .click();
        waitSeconds(10);
    }

    public void sendControlT() throws AWTException {
        Robot robot = new Robot();
        // If Mac OS X, the key combination is CMD+t, otherwise is CONTROL+t
        int vkControl = IS_OS_MAC ? VK_META : VK_CONTROL;
        robot.keyPress(vkControl);
        robot.keyPress(VK_T);
        robot.keyRelease(vkControl);
        robot.keyRelease(VK_T);
    }
    
    public String getCurrentUrl(ChromeDriver driver, String initialUrl) {
        WebDriverWait wait = new WebDriverWait(driver, 10); // seconds
        wait.until(not(urlToBe(initialUrl)));
        String sessionUrl = driver.getCurrentUrl();
        log.debug("Room URL {}", sessionUrl);
        return sessionUrl;
    }

}