# naver-login
###  This project **enables naver login session**, and **facilitates page access, which requires user-login.**

_**NOTE**: Before running the project install. (If you are a mac user, enter brew install chromedriver in terminal.)_

_possible erorr message: "The path to the driver executable must be set by the webdriver.chrome.driver system propert"_

- ### HOW-TO-RUN
 _Get a html document from a target url from naver main page after logged in._
```
    public void logInAndGetDocumentFromTargetUrl() throws NaverLoginFailException {
            NaverClient naverClient = new NaverClient();
            naverClient.tryLogin("userId", "password");
            Document pageDocument = naverClient.getPageDocument("http://www.naver.com");
            System.out.println(pageDocument.title());
    }
    public void getDocuemntofIframe() {
            NaverClient naverClient = new NaverClient();
            Document iframeDocument = naverClient.getIframe("http://www.naver.com", "iframeNameOrId");
            System.out.println(iframeDocument.title());
    }
```

### How To Import  (For more : [Kims-DeveloperGroup/naver-login repository]( https://jitpack.io/#Kims-DeveloperGroup/naver-login/))

- gradle
```
repositories {
			...
			maven { url 'https://jitpack.io' }
		}
dependencies {
	        compile 'com.github.Kims-DeveloperGroup:naver-login:v0.0.1-ML1'
	}
```

- maven
```
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
...
<dependency>
	    <groupId>com.github.Kims-DeveloperGroup</groupId>
	    <artifactId>naver-login</artifactId>
	    <version>v0.0.1-ML1</version>
	</dependency>
```
