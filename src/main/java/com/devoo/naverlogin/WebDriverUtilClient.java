package com.devoo.naverlogin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    public Document getPageDocument(String pageUrl) {
        webDriver.get(pageUrl);
        return Jsoup.parse(this.webDriver.getPageSource());
    }

    /**
     * Gets document of iframe
     *
     * @param pageUrl    page url of iframe
     * @param iframeName iframe name or id to crawl
     * @return document of iframe
     */
    public Document getIframe(String pageUrl, String iframeName) {
        this.webDriver.get(pageUrl);
        this.webDriver.switchTo().frame(iframeName);
        return Jsoup.parse(this.webDriver.getPageSource());
    }

    public void closeAlert() {
        this.webDriver.switchTo().alert().dismiss();
    }
}
