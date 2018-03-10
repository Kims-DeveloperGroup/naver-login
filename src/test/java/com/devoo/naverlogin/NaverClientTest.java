package com.devoo.naverlogin;

import com.devoo.naverlogin.exception.NaverLoginFailException;
import org.junit.Test;
import org.openqa.selenium.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class NaverClientTest {

    private final String ID = "th1nk";
    private final String PASSWORD = "rNVKFL!12";

    @Test
    public void shouldBeLoginUserInfoSectionFound_whenLoginSucceeds() throws NaverLoginFailException {
        //Given
        NaverClient naverClient = new NaverClient();
        String loginUserInfoElementClassName = "section_minime";

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
        String naverMainUrl = "https://www.naver.com";
        naverClient.openNewTab(naverMainUrl);

        //Then
        assertEquals(webDriver.findElement(By.id("account"))
                .getAttribute("class"), loginUserInfoElementClassName);
    }
}