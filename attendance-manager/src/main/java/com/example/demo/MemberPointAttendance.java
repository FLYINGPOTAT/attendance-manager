package com.example.demo;

public class MemberPointAttendance{
	private String name;
	private int point;
	private String[] list;
	
	public MemberPointAttendance(String name, int point, String[] list){
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
	
	public String[] list(){
		return list;
	}
	
	void addPoint(Integer p){
		point+=p;
	}
}