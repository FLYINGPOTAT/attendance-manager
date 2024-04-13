package com.example.demo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendancePoint {
	private String attend;
	private String point;
	
	AttendancePoint(String attend, String point){
		this.attend=attend;
		this.point=point;
	}
}
