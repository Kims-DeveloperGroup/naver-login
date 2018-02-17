package sample;

import com.devoo.naverlogin.NaverLogin;
import com.devoo.naverlogin.NaverPageCrawler;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

public class HowToRun {

    /**
     * Get html document from a target url from naver main page after logged in.
     */
    public void logInAndGetDocumentFromTargetUrl() {
        NaverLogin naverLogin = new NaverLogin();
        WebDriver loggedInWebDriver = naverLogin.tryLogin("userId", "password");
        NaverPageCrawler naverPageCrawler = new NaverPageCrawler();
        Document document = naverPageCrawler.getDocument(loggedInWebDriver, "https://www.naver.com");
    }
}
