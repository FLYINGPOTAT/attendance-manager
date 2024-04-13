package com.example.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AMController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping()
    public String home(Model model) {
    	LocalDateTime nowDate = LocalDateTime.now();
    	Integer year = nowDate.getYear();
    	Integer month = nowDate.getMonth().getValue();
    	String attSql = "SELECT member.name, attendance_status.day, attendance_status.attendance "
    			+ "FROM member, attendance_status "
    			+ "WHERE member.id = attendance_status.id AND attendance_status.year = "
    			+ year + " AND attendance_status.month = " + month;
    	var attList = jdbcTemplate.queryForList(attSql);
    	var memberPoints = jdbcTemplate.query("select name from member",
    			(rs,rowNum) -> new MemberPoint(rs.getString("name"),0));
    	
    	var attendanceData = new ArrayList<ArrayList<String>>();
//    	var members = new ArrayList<String>();
    	
    	for (var i=0; i<memberPoints.size();i++) {
    		var m = memberPoints.get(i);
    		var name=m.name();
    		for (Map<String, Object> e: attList){
    			if (e.get("name").equals(name)){
//    				members.add(name);
    				if (attendanceData.size()-1<i) {
    					attendanceData.add(new ArrayList<String>());
    				}
    				Integer point = jdbcTemplate.queryForObject(
    						"select point from attendance_point where attendance = '"
    						+ e.get("attendance")+"'",int.class);
    				m.addPoint(point);
    				var status = e.get("attendance_status");
    				if (status == null){
    					attendanceData.get(i).add("出席");
    				}else {
    					attendanceData.get(i).add((status).toString());
    				}
    			}
    		}
    	}
    	model.addAttribute("memberPoints", memberPoints);
    	model.addAttribute("attendanceData", attendanceData);
    	return "home";
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
    	m.addObject("attendancePoints",attendancePoints);
		m.setViewName("AttendPointSetting.html");
		return m;
	}
    @PostMapping("/APSetting")
	public void APSetting(@ModelAttribute AttendancePoint ap) {
    	String sql= "INSERT INTO attendance_point(attendance, point) VALUES('"+ap.getAttend()+"', "+ap.getPoint()+")";
    	jdbcTemplate.execute(sql);
	}
    
    @RequestMapping("/AttendanceRegistrationScreen")
	public String AttendanceRegistrationScreen() {
    	return "AttendanceRegistrationScreen";
	}
//    @PostMapping("/AttendanceRegister")
//	public void AttendanceRegister(@ModelAttribute MemberGrade mg) {
//    	String sql= "INSERT INTO member(name, grade) VALUES('"+mg.getName()+"', "+mg.getGrade()+")";
//    	jdbcTemplate.execute(sql);
//	}
}
