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
public class ResponseDTO implements Serializable {
	private String firstname;
	private String lastname;
	private int totalprice;
	private boolean depositpaid;
	private BookingdatesResponse bookingdates;
	private String additionalneeds;

}