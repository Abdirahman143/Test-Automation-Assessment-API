package com.ncba.testAPI;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.ncba.api.BookingRoute;
import com.ncba.pojo.Request.AuthTokenRequest;
import com.ncba.pojo.Request.BookingDatesRequest;
import com.ncba.pojo.Request.BookingId;
import com.ncba.pojo.Request.CreateBookingRequest;
import com.ncba.pojo.Responose.BookingResponse;
import com.ncba.pojo.Responose.CreateBookingResponse;
import com.ncba.utilsTest.ExtentManager;
import com.ncba.utilsTest.LocalDateAdapter;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingTest {
    private ExtentReports extent;
    private ExtentTest test;
    CreateBookingRequest request;
    private  final  static Logger logger = LoggerFactory.getLogger(BookingTest.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeMethod()
    public void setUp() {
        RestAssured.baseURI = BookingRoute.BASE_URL + "/booking";
        extent = ExtentManager.getInstance();
    }

    @Test(priority = 1,description = "Verify it returns all the booking Id")
    public void testFetchBookingIds() {
        test = extent.createTest("Verify it returns all the booking Id");

        Response response = given().get("/");

        // Assert status code
        Assertions.assertThat(response.getStatusCode()).as("Check status code").isEqualTo(200);

        // Deserialize JSON response to list of BookingId objects
        List<BookingId> bookingIds = response.jsonPath().getList("", BookingId.class);

        //assertion
        Assertions.assertThat(bookingIds).as("Check if booking ID list is not empty").isNotEmpty();
        Assertions.assertThat(bookingIds).as("Check each booking ID")
                .allSatisfy(bookingId -> Assertions.assertThat(bookingId.getBookingid()).isGreaterThan(0));

        // Log response in report
        test.log(Status.INFO, "Response: " + response.asString());
    }
    @Test(priority = 2,description = "Verify it fails to return booking Ids for an invalid endpoint")
    public void testFetchBookingIdsNegative() {
        test = extent.createTest("Verify it fails to return booking Ids for an invalid endpoint");

        Response response = given().get("/invalidEndpoint");

        // Assert that the status code indicates a client or server error
        Assertions.assertThat(response.getStatusCode()).as("Check for client or server error status code")
                .isGreaterThanOrEqualTo(400)
                .isLessThan(600);

        // Check if the response is JSON before parsing
        if ("application/json".equals(response.getContentType())) {
            List<BookingId> bookingIds = response.jsonPath().getList("", BookingId.class);

            // Assert that the booking IDs list is empty or null
            Assertions.assertThat(bookingIds).as("Check if booking ID list is empty").isEmpty();
        } else {
            // Handle non-JSON response
            test.log(Status.INFO, "Non-JSON Response: " + response.asString());
        }

        // Log response in report
        test.log(Status.INFO, "Response: " + response.asString());
    }





    @Test(priority = 3,description = "Get booking details with valid booking id should return success")
    public void testGetBookingDetailsPositive() {
        test = extent.createTest("Get booking details with valid booking id should return success");
        Response response = RestAssured.get("/1");

        // Assert status code
        assertThat(response.getStatusCode()).as("Check status code").isEqualTo(200);

        // Deserialize JSON response to BookingDetails object
        BookingResponse bookingDetails = response.as(BookingResponse.class);

        // Assert that booking details are not null or empty
        assertThat(bookingDetails.getFirstname()).as("Check firstname").isNotNull().isNotEmpty();
        assertThat(bookingDetails.getLastname()).as("Check lastname").isNotNull().isNotEmpty();
        assertThat(bookingDetails.getTotalprice()).as("Check totalprice").isGreaterThan(0);
        assertThat(bookingDetails.isDepositpaid()).as("Check depositpaid").isNotNull();
        assertThat(bookingDetails.getBookingdates()).as("Check bookingdates").isNotNull();
        assertThat(bookingDetails.getBookingdates().getCheckin()).as("Check checkin date").isNotNull().isNotEmpty();
        assertThat(bookingDetails.getBookingdates().getCheckout()).as("Check checkout date").isNotNull().isNotEmpty();

    }

    @Test(priority = 4,description = "Get booking details with Invalid booking id should not return success")
    public void testGetBookingDetailsNegative() {
        test = extent.createTest("Get booking details with Invalid booking id should not return success");
        int wrongId = 19888;

        // Send a request
        Response response = RestAssured.get("/" + wrongId);

        // Assert status code 404
        assertThat(response.getStatusCode()).as("Check status code").isEqualTo(404);

        // Assert the response body contains an error message (customize this assertion based on your API response format)
        String responseBody = response.getBody().asString();
        assertThat(responseBody).as("Check response body").contains("Not Found");

    }



    @Test(priority = 5,description ="Verify that create booking with correct data should return success" )
    public void testCreateBookingSuccess() throws JsonProcessingException {
        test =extent.createTest("Verify that create booking with correct data should return success");
        logger.info("..........Creating Booking details");
        // Create a new booking request
         request = CreateBookingRequest.builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(BookingDatesRequest.builder()
                        .checkin(LocalDate.parse("2018-01-01"))
                        .checkout(LocalDate.parse("2019-01-01"))
                        .build())
                .additionalneeds("Breakfast")
                .build();
        objectMapper.registerModule(new JavaTimeModule());
   String validJson = objectMapper.writeValueAsString(request);
        // Send the POST request
        Response response = given()
                .contentType("application/json")
                .body(validJson)
                .post(RestAssured.baseURI);
        logger.info("the response boddy"+response);

        // Deserialize JSON response to BookingResponse object
        CreateBookingResponse bookingResponse = response.as(CreateBookingResponse.class);

        // Assert status code
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Assert booking details
        assertThat(bookingResponse.getBookingid()).isGreaterThan(0);
        assertThat(bookingResponse.getBooking().getFirstname()).isEqualTo("Jim");
        assertThat(bookingResponse.getBooking().getLastname()).isEqualTo("Brown");
        assertThat(bookingResponse.getBooking().getTotalprice()).isEqualTo(111);
        assertThat(bookingResponse.getBooking().isDepositpaid()).isTrue();
        assertThat(bookingResponse.getBooking().getAdditionalneeds()).isEqualTo("Breakfast");
    }


    @Test(priority = 6, description = "Verify that create booking with incorrect data should return an error")
    public void testCreateBookingFailure() throws JsonProcessingException {
        test = extent.createTest("Verify that create booking with incorrect data should return an error");
        logger.info("..........Creating Booking details");

        // Create a new booking request with incorrect data (e.g., missing required fields)
        request = CreateBookingRequest.builder()
                .totalprice(111) // Missing firstname, lastname, and other required fields
                .build();

        objectMapper.registerModule(new JavaTimeModule());
        String invalidJson = objectMapper.writeValueAsString(request);

        // Send the POST request
        Response response = given()
                .contentType("application/json")
                .body(invalidJson)
                .post(RestAssured.baseURI);
        logger.info("response body"+response);

        logger.info("the response body: " + response);

        // Assert status code (it should be a client error, e.g., 4xx)
        assertThat(response.getStatusCode()).isBetween(500,599); // Expecting a client error


    }





    @Test(priority = 7, description = "Update a booking")
    public void testUpdateBooking() {
        test = extent.createTest("Update a booking");
        logger.info("Updating Booking details");

        // Create a new booking request
        LocalDate checkin = LocalDate.parse("2018-01-01");
        LocalDate checkout = LocalDate.parse("2019-01-01");

        CreateBookingRequest request = CreateBookingRequest.builder()
                .firstname("James")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(BookingDatesRequest.builder()
                        .checkin(checkin)
                        .checkout(checkout)
                        .build())
                .additionalneeds("Breakfast")
                .build();

        // Use a custom Gson instance to handle LocalDate serialization
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .create();

        String validJson = gson.toJson(request);
        logger.info("Auth token ........."+ AuthTokenTest.authToken);

        // Send the PUT request
        String base64Credentials = "YWRtaW46cGFzc3dvcmQxMjM=";

// Send the PUT request with Basic authorization
        Response response = given()
                .header("Authorization", "Basic " + base64Credentials) // Set Basic authorization header
                .contentType("application/json")
                .body(validJson)
                .put(RestAssured.baseURI +"/1");

        logger.info("Response body: " + response.getBody().asString());

        // Assert status code
        assertThat(response.getStatusCode()).isEqualTo(200);


    }

    @Test(priority = 7, description = "Partial Update a booking")
    public void testPartialUpdateBooking() {
        test = extent.createTest("Partial Update a booking");
        logger.info("Partially Updating Booking details");

        // Define the booking ID you want to update
        int bookingId = 1;

        // Define the fields to update in the request body
        String requestBody = "{\n" +
                "    \"firstname\": \"James\",\n" +
                "    \"lastname\": \"Brown\"\n" +
                "}";

        // Set the authentication token from your test class
        String authToken = AuthTokenTest.authToken;

        // Send the PATCH request
        Response response = given()
                .baseUri(RestAssured.baseURI)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + authToken) // Use Cookie for authorization, or use Authorization header if needed
                .body(requestBody)
                .patch("/" + bookingId); // Append the bookingId to the base path

        logger.info("Response body: " + response.getBody().asString());

        // Assert status code (HTTP 200 OK for success)
        assertThat(response.getStatusCode()).isEqualTo(200);

        // You can add additional assertions here if needed
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