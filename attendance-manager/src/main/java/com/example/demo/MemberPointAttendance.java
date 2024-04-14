package com.example.demo;

import java.util.ArrayList;

public class MemberPointAttendance{
	private String name;
	private int point;
	private ArrayList<String> list;
	
	public MemberPointAttendance(String name, int point, ArrayList<String> list){
		this.name = name;
		this.point = point;
		this.list = list;
	}
	public String name(){
		return name;
	}
	
	public Integer point() {
		return point;
	}
	
	public ArrayList<String> list(){
		return list;
	}
	
	void addPoint(Integer p){
		point+=p;
	}
}