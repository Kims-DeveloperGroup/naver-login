package com.devoo.naverlogin;

import com.devoo.naverlogin.exception.NaverLoginFailException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class NaverLogin {
    static final String LOGIN_USERINFO_ELEMENT_CLASSNAME = "section_minime";

    private final String NAVER_HOME_URL = "https://www.naver.com/";
    private final String LOGIN_BUTTON_SELECTOR = "#frmNIDLogin fieldset .btn_login input";
    private String LOGIN_FORM_ID = "frmNIDLogin";
    private WebDriver webDriver;
    private List<String> windowHandles;

    public NaverLogin() {
        this.webDriver = new ChromeDriver();
    }

    public NaverLogin(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public WebDriver getWebDriver() {
        return this.webDriver;
    }

    public NaverLogin tryLogin(String id, String password) throws NaverLoginFailException {
        this.webDriver.get(NAVER_HOME_URL);
        new WebDriverWait(this.webDriver, 5)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id(LOGIN_FORM_ID)));
        this.webDriver.findElement(By.id("id")).sendKeys(id);
        this.webDriver.findElement(By.id("pw")).sendKeys(password);
        this.webDriver.findElement(By.cssSelector(LOGIN_BUTTON_SELECTOR)).click();
        if (!isLoginSucceeded()) {
            throw new NaverLoginFailException();
        }
        return this;
    }

    public NaverLogin openNewTab(String url) {
        ((JavascriptExecutor)webDriver).executeScript("window.open()");
        this.windowHandles = new ArrayList<>(this.webDriver.getWindowHandles());
        this.webDriver.switchTo().window(windowHandles.get(windowHandles.size()-1));
        this.webDriver.get(url);
        return this;
    }

    public boolean isLoginSucceeded() {
        try {
            String classNameOfLoginElement =
                    webDriver.findElement(By.id("account")).getAttribute("class");
            if (classNameOfLoginElement.equals(LOGIN_USERINFO_ELEMENT_CLASSNAME)) {
                return true;
            }
        }catch (NoSuchElementException e) {
            return false;
        }
        return false;
    }
}