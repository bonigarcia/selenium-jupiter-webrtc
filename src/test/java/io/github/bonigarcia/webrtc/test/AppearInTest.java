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

import static io.github.bonigarcia.seljup.BrowserType.CHROME;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.seljup.Arguments;
import io.github.bonigarcia.seljup.DockerBrowser;
import io.github.bonigarcia.webrtc.base.WebRtcBase;

class AppearInTest extends WebRtcBase {

    static final String APP_URL = "https://appear.in/selenium-jupiter";

    @Test
    void testAppearIn(
            @Arguments({ "--use-fake-device-for-media-stream",
                    "--use-fake-ui-for-media-stream" }) ChromeDriver driver,
            @Arguments({ "--use-fake-device-for-media-stream",
                    "--use-fake-ui-for-media-stream" })
            @DockerBrowser(type = CHROME, size = NUM_VIEWERS) List<WebDriver> driverList)
            throws Exception {
        log.debug("Benchmarking WebRTC room at {}", APP_URL);

        // Open webrtc-internals in second tab and return to first one
        openWebRtcInternals(driver);

        // Presenter
        log.debug("Entering {}", driver);
        execute(() -> driver.get(APP_URL));

        // Viewers
        for (WebDriver wd : driverList) {
            log.debug("Waiting {} seconds for new browser", BROWSERS_RATE_SEC);
            waitSeconds(BROWSERS_RATE_SEC);

            log.debug("Entering {}", wd);
            execute(() -> wd.get(APP_URL));
        }

        // Wait session time
        log.debug("Waiting {} seconds with all participants", SESSION_TIME_SEC);
        waitSeconds(SESSION_TIME_SEC);

        // Download WebRC stats
        downloadStats(driver);
    }

}
