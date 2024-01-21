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
public class BookingdatesResponse implements Serializable {
	private String checkin;
	private String checkout;

}