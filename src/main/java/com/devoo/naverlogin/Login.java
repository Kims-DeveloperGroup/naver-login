package com.devoo.naverlogin;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Login {

    private final String NAVER_HOME_URL = "https://www.naver.com/";
    private final String LOGIN_BUTTON_SELECTOR = "#frmNIDLogin fieldset .btn_login input";

    public WebDriver tryLogin(String id, String password) {
        ChromeDriver chromeDriver = new ChromeDriver();
        chromeDriver.get(NAVER_HOME_URL);

        chromeDriver.findElementById("id").sendKeys(id);
        chromeDriver.findElementById("pw").sendKeys(password);
        chromeDriver
                .findElementByCssSelector(LOGIN_BUTTON_SELECTOR).click();
        return chromeDriver;
    }
}