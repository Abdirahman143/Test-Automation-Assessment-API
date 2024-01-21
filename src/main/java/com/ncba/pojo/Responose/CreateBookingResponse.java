package com.ncba.pojo.Responose;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBookingResponse implements Serializable {
    private int bookingid;
    private Booking booking;



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Booking {
        private String firstname;
        private String lastname;
        private int totalprice;
        private boolean depositpaid;
        private BookingDatesResponse bookingdates;
        @JsonProperty("additionalneeds") // Map the JSON field to the Java field
        private String additionalneeds;
    }
}
