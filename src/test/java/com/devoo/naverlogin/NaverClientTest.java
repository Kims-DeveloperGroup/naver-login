package com.devoo.naverlogin;

import com.devoo.naverlogin.exception.NaverLoginFailException;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NaverClientTest {

    private final String ID = "CREDENTIAL";
    private final String PASSWORD = "CREDENTIAL";
    private final String naverMainUrl = "https://www.naver.com";

    @Test
    public void shouldBeLoginUserInfoSectionFound_whenLoginSucceeds() throws NaverLoginFailException {
        //Given
        NaverClient naverClient = new NaverClient();

        //When
        WebDriver webDriver = naverClient.tryLogin(ID, PASSWORD).getWebDriver();
        List<WebElement> spans = webDriver.findElements(By.tagName("span"));

        //Then
        assertEquals(webDriver.findElement(By.id("account"))
                .getAttribute("class"), NaverClient.LOGIN_USERINFO_ELEMENT_CLASSNAME);
    }

    @Test(expected = NaverLoginFailException.class)
    public void shouldBeLoginFailExceptionThrown_whenLoginFails() throws NaverLoginFailException {
        //Given
        NaverClient naverClient = new NaverClient();
        String incorrectId = "incorrect";
        String incorrectPassword = "incorrect";
        //When
        naverClient.tryLogin(incorrectId, incorrectPassword);
    }


    @Test
    public void shouldBeLoginSessionContinued_whenLogInIsSuccessAndNewTabIsOpened() throws NaverLoginFailException {
        //Given
        NaverClient naverClient = new NaverClient();
        String loginUserInfoElementClassName = "section_minime";
        WebDriver webDriver = naverClient.tryLogin(ID, PASSWORD).getWebDriver();

        //When
        naverClient.openNewTab(naverMainUrl);

        //Then
        assertEquals(webDriver.findElement(By.id("account"))
                .getAttribute("class"), loginUserInfoElementClassName);
    }

    @Test
    public void shouldBeDocumentInsideIframeRetrieved() {
        //Given
        NaverClient naverClient = new NaverClient();
        //When
        Document da_iframe_rolling = naverClient.getIframe(naverMainUrl, "da_iframe_rolling");
        //then
        assertNotNull(da_iframe_rolling.body());
    }
}