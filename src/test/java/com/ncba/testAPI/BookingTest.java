package com.ncba.testAPI;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.ncba.api.BookingRoute;
import com.ncba.pojo.Responose.ResponseDTO;
import com.ncba.utilsTest.ExtentManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BookingTest {
    private ExtentReports extent;
    private ExtentTest test;

    @BeforeMethod()
    public void setUp() {
        RestAssured.baseURI = BookingRoute.BASE_URL + "/booking";
        extent = ExtentManager.getInstance();
    }

    @Test
    public void testGetBookingDetailsPositive() {
        test = extent.createTest("Get booking details with valid booking id should return success");
        Response response = RestAssured.get("/1");

        // Assert status code
        assertThat(response.getStatusCode()).as("Check status code").isEqualTo(200);

        // Deserialize JSON response to BookingDetails object
        ResponseDTO bookingDetails = response.as(ResponseDTO.class);

        // Extract expected values from the response
        String expectedFirstname = bookingDetails.getFirstname();
        String expectedLastname = bookingDetails.getLastname();
        int expectedTotalprice = bookingDetails.getTotalprice();
        boolean expectedDepositpaid = bookingDetails.isDepositpaid();
        String expectedCheckinDate = bookingDetails.getBookingdates().getCheckin();
        String expectedCheckoutDate = bookingDetails.getBookingdates().getCheckout();
        String expectedAdditionalneeds = bookingDetails.getAdditionalneeds();

        // Assert booking details
        assertThat(expectedFirstname).as("Check firstname").isEqualTo("Mary");
        assertThat(expectedLastname).as("Check lastname").isEqualTo("Wilson");
        assertThat(expectedTotalprice).as("Check totalprice").isEqualTo(726);
        assertThat(expectedDepositpaid).as("Check depositpaid").isTrue();
        assertThat(expectedCheckinDate).as("Check checkin date").isEqualTo("2022-10-04");
        assertThat(expectedCheckoutDate).as("Check checkout date").isEqualTo("2023-12-07");
        assertThat(expectedAdditionalneeds).as("Check additionalneeds").isEqualTo("Breakfast");
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