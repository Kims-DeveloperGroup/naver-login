package com.devoo.naverlogin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NaverLogin {

    private final String NAVER_HOME_URL = "https://www.naver.com/";
    private final String LOGIN_BUTTON_SELECTOR = "#frmNIDLogin fieldset .btn_login input";
    private String LOGIN_FORM_ID = "frmNIDLogin";

    public WebDriver tryLogin(String id, String password) {
        ChromeDriver chromeDriver = new ChromeDriver();
        chromeDriver.get(NAVER_HOME_URL);
        new WebDriverWait(chromeDriver, 5)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id(LOGIN_FORM_ID)));
        chromeDriver.findElementById("id").sendKeys(id);
        chromeDriver.findElementById("pw").sendKeys(password);
        chromeDriver
                .findElementByCssSelector(LOGIN_BUTTON_SELECTOR).click();
        return chromeDriver;
    }
}