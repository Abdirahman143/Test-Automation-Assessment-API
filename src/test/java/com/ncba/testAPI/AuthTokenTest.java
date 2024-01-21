package com.ncba.testAPI;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.ncba.api.BookingRoute;
import com.ncba.pojo.Request.AuthTokenRequest;
import com.ncba.pojo.Responose.AuthTokenResponse;
import com.ncba.utilsTest.ExtentManager;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthTokenTest {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenTest.class);
    private AuthTokenRequest authTokenRequest;
    private ExtentReports extent;
    private ExtentTest test;


    @BeforeMethod
    public void setUp() {
        authTokenRequest = AuthTokenRequest.builder()
                .username("admin")
                .password("password123")
                .build();
        extent = ExtentManager.getInstance();
    }

    @Test(priority = 1)

    public void testAuthTokenGeneration() {
     test= extent.createTest("Verify user can generate token after passing valid username and password");
        Response response = given()
                .contentType("application/json")
                .body(authTokenRequest)
                .when()
                .post(BookingRoute.BASE_URL + "/auth");

        AuthTokenResponse authTokenResponse = response.as(AuthTokenResponse.class);

        logger.info("Auth token received: {}", authTokenResponse.getToken());

        assertThat(authTokenResponse.getToken()).as("Check if token is not null or empty")
                .isNotNull()
                .isNotEmpty();


    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "Failed: " + result.getName());
            test.log(Status.FAIL, "Cause: " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.log(Status.SKIP, "Skipped: " + result.getName());
        } else {
            test.log(Status.PASS, "Passed:  " + result.getName());
        }
        extent.flush();
    }
}