package com.devoo.naverlogin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

public class NaverPageCrawler {

    public Document getDocument(WebDriver webDriver, String targetUrl) {
        webDriver.get(targetUrl);
        return Jsoup.parse(webDriver.getPageSource());
    }
}