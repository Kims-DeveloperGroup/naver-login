package com.devoo.naverlogin;

import com.devoo.naverlogin.exception.NaverLoginFailException;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class NaverLoginTest {

    private final String ID = "th1nk";
    private final String PASSWORD = "rNVKFL!12";

    @Test
    public void shouldBeLoginUserInfoSectionFound_whenLoginSucceeds() throws NaverLoginFailException {
        //Given
        NaverLogin naverLogin = new NaverLogin();
        String loginUserInfoElementClassName = "section_minime";

        //When
        WebDriver webDriver = naverLogin.tryLogin(ID, PASSWORD).getWebDriver();
        List<WebElement> spans = webDriver.findElements(By.tagName("span"));

        //Then
        assertEquals(webDriver.findElement(By.id("account"))
                .getAttribute("class"), NaverLogin.LOGIN_USERINFO_ELEMENT_CLASSNAME);
    }

    @Test(expected = NaverLoginFailException.class)
    public void shouldBeLoginFailExceptionThrown_whenLoginFails() throws NaverLoginFailException {
        //Given
        NaverLogin naverLogin = new NaverLogin();
        String incorrectId = "incorrect";
        String incorrectPassword = "incorrect";
        //When
        naverLogin.tryLogin(incorrectId, incorrectPassword);
    }


    @Test
    public void shouldBeLoginSessionContinued_whenLogInIsSuccessAndNewTabIsOpened() throws NaverLoginFailException {
        //Given
        NaverLogin naverLogin = new NaverLogin();
        String loginUserInfoElementClassName = "section_minime";
        WebDriver webDriver = naverLogin.tryLogin(ID, PASSWORD).getWebDriver();

        //When
        String naverMainUrl = "https://www.naver.com";
        naverLogin.openNewTab(naverMainUrl);

        //Then
        assertEquals(webDriver.findElement(By.id("account"))
                .getAttribute("class"), loginUserInfoElementClassName);
    }
}