package sample;

import com.devoo.naverlogin.NaverClient;
import com.devoo.naverlogin.NaverPageCrawler;
import com.devoo.naverlogin.exception.NaverLoginFailException;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

public class HowToRun {

    /**
     * Get html document from a target url from naver main page after logged in.
     */
    public void logInAndGetDocumentFromTargetUrl() throws NaverLoginFailException {
        NaverClient naverClient = new NaverClient();
        WebDriver loggedInWebDriver = naverClient.tryLogin("userId", "password").getWebDriver();
        NaverPageCrawler naverPageCrawler = new NaverPageCrawler();
        Document document = naverPageCrawler.getDocument(loggedInWebDriver, "https://www.naver.com");
    }
}
