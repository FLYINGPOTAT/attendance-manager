package com.example.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AMController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping()
    public ModelAndView home(ModelAndView model) {
    	LocalDateTime nowDate = LocalDateTime.now();
    	Integer year = nowDate.getYear();
    	Integer month = nowDate.getMonth().getValue();
    	String attSql = "SELECT member.name, attendance_status.day, attendance_status.attendance "
    			+ "FROM member, attendance_status "
    			+ "WHERE member.id = attendance_status.id AND attendance_status.year = "
    			+ year + " AND attendance_status.month = " + month;
    	var attList = jdbcTemplate.queryForList(attSql);
    	var memberHistoryData = jdbcTemplate.query("select name from member",
    			(rs,rowNum) -> new MemberPointAttendance(rs.getString("name"),0,new ArrayList<String>()));
    	
    	for (var i=0; i<memberHistoryData.size();i++) {
    		var m = memberHistoryData.get(i);
    		var name=m.name();
    		for (Map<String, Object> e: attList){
    			if (e.get("name").equals(name)){
    				Integer point = jdbcTemplate.queryForObject(
    						"select point from attendance_point where attendance = '"
    						+ e.get("attendance")+"'",int.class);
    				m.addPoint(point);
    				var status = e.get("attendance_status");
    				if (status == null){
    					memberHistoryData.get(i).list().add("出席");
    				}else {
    					memberHistoryData.get(i).list().add(status.toString());
    				}
    			}
    		}
    	}
    	model.addObject("memberHistoryData", memberHistoryData);
    	model.setViewName("home");
    	return model;
    }
    @RequestMapping("/MemberRegistrationScreen")
	public String memScreen() {
    	return "memberRegister";
	}
    @PostMapping("/memberRegister")
	public void memberRegister(@ModelAttribute MemberGrade mg) {
    	String sql= "INSERT INTO member(name, grade) VALUES('"+mg.getName()+"', "+mg.getGrade()+")";
    	jdbcTemplate.execute(sql);
	}
    @RequestMapping("/AttendPointSetting")
	public ModelAndView attendPointSettingScreen(ModelAndView m) {
    	var attendancePoints = jdbcTemplate.query("select * from attendance_point",
    			(rs,rowNum) -> new AttendancePoint(rs.getString("attendance"),rs.getString("point")));
    	for (var i=0;i<attendancePoints.size();i++) {
    		System.out.println(attendancePoints.get(i).getAttend());
    	}
    	
    	m.addObject("attendancePoints",attendancePoints);
		m.setViewName("AttendPointSetting");
		return m;
	}
    @PostMapping("/APSetting")
	public String APSetting(@ModelAttribute AttendancePoint ap) {
    	String sql= "INSERT INTO attendance_point(attendance, point) VALUES('"+ap.getAttend()+"', "+ap.getPoint()+")";
    	jdbcTemplate.execute(sql);
    	return "redirect:/AttendPointSetting";
	}
    
//    @RequestMapping("/AttendanceRegistrationScreen")
//	public String AttendanceRegistrationScreen() {
//    	return "AttendanceRegistrationScreen";
//	}
//    @PostMapping("/AttendanceRegister")
//	public void AttendanceRegister(@ModelAttribute MemberGrade mg) {
//    	String sql= "INSERT INTO member(name, grade) VALUES('"+mg.getName()+"', "+mg.getGrade()+")";
//    	jdbcTemplate.execute(sql);
//	}
}
