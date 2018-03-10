package com.devoo.naverlogin;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class WebDriverUtilClient {
    private final WebDriver webDriver;
    private List<String> windowHandles;

    public WebDriverUtilClient(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public WebDriver getWebDriver() {
        return this.webDriver;
    }

    public void openNewTab(String url) {
        ((JavascriptExecutor) webDriver).executeScript("window.open()");
        this.windowHandles = new ArrayList<>(this.webDriver.getWindowHandles());
        this.webDriver.switchTo().window(windowHandles.get(windowHandles.size() - 1));
        this.webDriver.get(url);
    }
}
