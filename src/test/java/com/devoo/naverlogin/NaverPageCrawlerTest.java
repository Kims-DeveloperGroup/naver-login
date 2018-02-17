package com.devoo.naverlogin;

import org.jsoup.nodes.Document;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.assertEquals;

public class NaverPageCrawlerTest {

    private final NaverPageCrawler naverPageCrawler = new NaverPageCrawler();
    private final WebDriver webDriver = new ChromeDriver();

    @Test
    public void shouldPageSourceBeReturned_whenTargetUrlIsGiven() {
        //Given
        String targetUrl = "https://www.naver.com";
        String expectedTitle = "naver";

        //When
        Document document = naverPageCrawler.getDocument(webDriver, targetUrl);

        //Then
        assertEquals(expectedTitle, document.title().toLowerCase());
    }
}