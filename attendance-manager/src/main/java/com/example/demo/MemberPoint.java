package com.example.demo;

public class MemberPoint{
	private String name;
	private Integer point;
	
	public MemberPoint(String name, Integer point){
		this.name = name;
		this.point = point;
	}
	public String name(){
		return name;
	}
	
	public Integer point() {
		return point;
	}
	
	void addPoint(Integer p){
		point+=p;
	}
}