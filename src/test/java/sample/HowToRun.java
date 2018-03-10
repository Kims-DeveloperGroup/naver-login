package sample;

import com.devoo.naverlogin.NaverClient;
import com.devoo.naverlogin.exception.NaverLoginFailException;
import org.jsoup.nodes.Document;

public class HowToRun {

    /**
     * Get html document from a target url from naver main page after logged in.
     * Note: user id and password should be correct set or NaverLogingFail exception is thrown.
     */
    public void logInAndGetDocumentFromTargetUrl() throws NaverLoginFailException {
        NaverClient naverClient = new NaverClient();
        naverClient.tryLogin("userId", "password");
        Document pageDocuemnt = naverClient.getPageDocument("http://www.naver.com");
        System.out.println(pageDocuemnt.title());
    }

    public void getDocuemntofIframe() {
        NaverClient naverClient = new NaverClient();
        Document iframeDocument = naverClient.getIframe("http://www.naver.com", "iframeNameOrId");
        System.out.println(iframeDocument.title());
    }
}
