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
package io.github.bonigarcia.webrtc.test;

import static java.lang.invoke.MethodHandles.lookup;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;
import static org.slf4j.LoggerFactory.getLogger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import io.github.bonigarcia.Arguments;
import io.github.bonigarcia.SeleniumExtension;
import io.github.bonigarcia.webrtc.base.WebRtcBase;

@ExtendWith(SeleniumExtension.class)
class ApprTcTest extends WebRtcBase {

    final Logger log = getLogger(lookup().lookupClass());

    @Test
    void testApprTc(
            @Arguments({ "--use-fake-device-for-media-stream",
                    "--use-fake-ui-for-media-stream" }) ChromeDriver driver1,
            @Arguments({ "--use-fake-device-for-media-stream",
                    "--use-fake-ui-for-media-stream" }) WebDriver driver2)
            throws Exception {

        // Presenter
        String apprUrl = "https://appr.tc/";
        driver1.get(apprUrl);
        driver1.findElement(By.id("join-button")).click();

        // Wait for room URL
        WebDriverWait wait = new WebDriverWait(driver1, 10); // seconds
        wait.until(not(urlToBe(apprUrl)));
        String currentUrl = driver1.getCurrentUrl();
        log.debug("Room URL {}", currentUrl);

        // Viewer
        driver2.get(currentUrl);
        WebElement joinButton = driver2
                .findElement(By.id("confirm-join-button"));
        wait.until(ExpectedConditions.elementToBeClickable(joinButton));
        joinButton.click();
        waitSeconds(5);

        // Open webrtc-internals in new tab
        openWebRtcInternalsInNewTab(driver1);

        // Wait session time
        waitSeconds(10);

        // Download WebRC stats
        downloadStats(driver1);
    }

}
