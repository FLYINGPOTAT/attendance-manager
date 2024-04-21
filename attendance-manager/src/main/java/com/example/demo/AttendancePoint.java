package com.example.demo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendancePoint {
	private String attendance;
	private String point;
	
	AttendancePoint(String attendance, String point){
		this.attendance=attendance;
		this.point=point;
	}
	
	AttendancePoint(){}
}
