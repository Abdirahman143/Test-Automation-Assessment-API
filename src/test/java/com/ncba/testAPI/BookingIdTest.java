package com.ncba.testAPI;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.ncba.api.BookingRoute;
import com.ncba.pojo.Request.BookingId;
import com.ncba.utilsTest.ExtentManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class BookingIdTest {
    private ExtentReports extent;
    private ExtentTest test;

    @BeforeMethod()
    public void setUp() {
        RestAssured.baseURI = BookingRoute.BASE_URL + "/booking";
        extent = ExtentManager.getInstance();
    }

    @Test
    public void testFetchBookingIds() {
        test = extent.createTest("Verify it returns all the booking Id");

        Response response = RestAssured.given().get("/");

        // Assert status code
        assertThat(response.getStatusCode()).as("Check status code").isEqualTo(200);

        // Deserialize JSON response to list of BookingId objects
        List<BookingId> bookingIds = response.jsonPath().getList("", BookingId.class);

         //assertion
        assertThat(bookingIds).as("Check if booking ID list is not empty").isNotEmpty();
        assertThat(bookingIds).as("Check each booking ID")
                .allSatisfy(bookingId -> assertThat(bookingId.getBookingid()).isGreaterThan(0));

        // Log response in report
        test.log(Status.INFO, "Response: " + response.asString());
    }
    @Test
    public void testFetchBookingIdsNegative() {
        test = extent.createTest("Verify it fails to return booking Ids for an invalid endpoint");

        Response response = RestAssured.given().get("/invalidEndpoint");

        // Assert that the status code indicates a client or server error
        assertThat(response.getStatusCode()).as("Check for client or server error status code")
                .isGreaterThanOrEqualTo(400)
                .isLessThan(600);

        // Check if the response is JSON before parsing
        if ("application/json".equals(response.getContentType())) {
            List<BookingId> bookingIds = response.jsonPath().getList("", BookingId.class);

            // Assert that the booking IDs list is empty or null
            assertThat(bookingIds).as("Check if booking ID list is empty").isEmpty();
        } else {
            // Handle non-JSON response
            test.log(Status.INFO, "Non-JSON Response: " + response.asString());
        }

        // Log response in report
        test.log(Status.INFO, "Response: " + response.asString());
    }


    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "Failed: " + result.getName());
            test.log(Status.FAIL, "Cause: " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.log(Status.SKIP, "Skipped: " + result.getName());
        } else {
            test.log(Status.PASS, "Passed: " + result.getName());
        }
        extent.flush();
    }
}