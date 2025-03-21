/*
 * (C) Copyright 2021 Boni Garcia (https://bonigarcia.github.io/)
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

import static io.github.bonigarcia.seljup.BrowserType.CHROME;
import static java.util.UUID.randomUUID;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.seljup.Arguments;
import io.github.bonigarcia.seljup.DockerBrowser;
import io.github.bonigarcia.webrtc.base.WebRtcBase;

class JanusTest extends WebRtcBase {

    static final String APP_URL = "https://janus.conf.meetecho.com/videoroomtest.html";

    @Test
    void test(
            @Arguments({ "--use-fake-device-for-media-stream",
                    "--use-fake-ui-for-media-stream" }) ChromeDriver driver,
            @Arguments({ "--use-fake-device-for-media-stream",
                    "--use-fake-ui-for-media-stream" })
            @DockerBrowser(type = CHROME, size = NUM_PEERS) List<WebDriver> driverList)
            throws Exception {
        String roomName = randomUUID().toString().replace("-", "");
        log.debug("Benchmarking WebRTC room at {}", APP_URL);

        // 1. Open webrtc-internals
        openWebRtcInternals(driver);

        // 2. Enter room with local browser (monitor)
        log.debug("Entering {}", driver);
        execute(() -> {
            driver.get(APP_URL);
            enterRoom(driver, roomName);
        });

        // 3. Enter room with dockerized browsers (load)
        for (WebDriver wd : driverList) {
            log.debug("Waiting {} seconds for new browser", BROWSERS_RATE_SEC);
            waitSeconds(BROWSERS_RATE_SEC);

            log.debug("Entering {}", wd);
            execute(() -> {
                wd.get(APP_URL);
                enterRoom(wd, roomName);
            });
        }

        // 4. Wait session time
        log.debug("Waiting {} seconds with all participants", SESSION_TIME_SEC);
        waitSeconds(SESSION_TIME_SEC);

        // 5. Download WebRTC stats
        downloadStats(driver);
    }

    void enterRoom(WebDriver driver, String roomName) {
        driver.findElement(By.id("start")).click();

        By sessionSelector = By.id("username");
        WebDriverWait wait = new WebDriverWait(driver,
                Duration.ofSeconds(WAIT_TIME_SEC));
        wait.until(visibilityOfElementLocated(sessionSelector));

        driver.findElement(sessionSelector).click();
        driver.findElement(sessionSelector).sendKeys(roomName);

        driver.findElement(By.id("register")).click();
    }

}
