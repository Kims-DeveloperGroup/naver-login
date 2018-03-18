package com.devoo.naverlogin;

import com.devoo.naverlogin.exception.NaverLoginFailException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NaverClient extends WebDriverUtilClient {
    private static final Logger log = LoggerFactory.getLogger(NaverClient.class);

    static final String LOGIN_USERINFO_ELEMENT_CLASSNAME = "section_minime";

    private final String NAVER_HOME_URL = "https://www.naver.com/";
    private final String NAVER_LOGIN_PAGE_URL = "https://nid.naver.com/nidlogin.login";
    private final String LOGIN_BUTTON_SELECTOR = "#frmNIDLogin fieldset .btn_global";
    private String LOGIN_FORM_ID = "frmNIDLogin";
    private WebDriver webDriver;

    public NaverClient() {
        super(new ChromeDriver());
        this.webDriver = super.getWebDriver();
    }

    public NaverClient(WebDriver webDriver) {
        super(webDriver);
        this.webDriver = super.getWebDriver();
    }

    public NaverClient tryLogin(String id, String password) throws NaverLoginFailException {
        this.webDriver.get(NAVER_LOGIN_PAGE_URL);
        new WebDriverWait(this.webDriver, 5)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id(LOGIN_FORM_ID)));
        this.webDriver.findElement(By.id("id")).sendKeys(id);
        this.webDriver.findElement(By.id("pw")).sendKeys(password);
        this.webDriver.findElement(By.cssSelector(LOGIN_BUTTON_SELECTOR)).click();
        log.debug("Trying to log in {}", id);
        if (!isLoginSucceeded()) {
            log.debug("Loging fail : {}", id);
            throw new NaverLoginFailException();
        }
        return this;
    }

    public boolean isLoginSucceeded() {
        if (!this.webDriver.getCurrentUrl().equals(NAVER_HOME_URL)) {
            log.debug("The current url is not home: {}", this.webDriver.getCurrentUrl());
            return false;
        }
        new WebDriverWait(this.webDriver, 3)
                .until(ExpectedConditions.visibilityOfElementLocated(By.className(LOGIN_USERINFO_ELEMENT_CLASSNAME)));
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

    public void terminate() {
        this.webDriver.quit();
    }
}