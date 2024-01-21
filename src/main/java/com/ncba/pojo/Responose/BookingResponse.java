package com.ncba.pojo.Responose;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse implements Serializable {
    private int bookingid;
    private String firstname;
    private String lastname;
    private int totalprice;
    private boolean depositpaid;
    private BookingDatesResponse bookingdates;
    private String additionalneeds;

}


