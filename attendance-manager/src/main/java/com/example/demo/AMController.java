package com.example.demo;

import java.time.LocalDateTime;
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
    			(rs,rowNum) -> new MemberPointAttendance(rs.getString("name"),0,new String[31]));
    	
    	for (var i=0; i<memberHistoryData.size();i++) {
    		var m = memberHistoryData.get(i);
    		var name=m.name();
    		var statusList=memberHistoryData.get(i).list();
    		for (Map<String, Object> e: attList){
    			if (e.get("name").equals(name)){
    				int point = jdbcTemplate.queryForObject(
    						"select point from attendance_point where attendance = '"
    						+ e.get("attendance")+"'",int.class);
    				m.addPoint(point);
    				var status = e.get("attendance");
    				if (status != null){
    					statusList[(int)e.get("day")-1]=status.toString();
    				}
    			}
    		}
    		Integer day = nowDate.getDayOfMonth();
    		for(var x=0; x<31;x++) {
    			if(null == statusList[x]) {
    				if(x<day-1) {
    					statusList[x]="出席";
    				}else {
    					statusList[x]="未定";
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
//    @PostMapping("/memberRegister")
//    public void memberRegister(@ModelAttribute MemberGrade mg) {
//        String sql= "INSERT INTO member(name, grade) VALUES(?,?)";
//        System.out.println(""+mg.getName()+mg.getGrade());
//        jdbcTemplate.update(sql,mg.getName(),mg.getGrade());
    @PostMapping("/memberRegister")
	public void memberRegister(@ModelAttribute MemberGrade mg) {
    	String sql= "insert into member(name, grade) values('"+mg.getName()+"', "+mg.getGrade()+")";
    	System.out.println(""+mg.getName()+mg.getGrade());
    	jdbcTemplate.execute(sql);
	}
    @RequestMapping("/AttendPointSetting")
	public ModelAndView attendPointSettingScreen(ModelAndView m) {
    	var attendancePoints = jdbcTemplate.query("select * from attendance_point",
    			(rs,rowNum) -> new AttendancePoint(rs.getString("attendance"),rs.getString("point")));   	
    	m.addObject("attendancePoints",attendancePoints);
		m.setViewName("AttendPointSetting");
		return m;
	}
    @PostMapping("/APSetting")
	public String APSetting(@ModelAttribute AttendancePoint ap) {
    	String sql= "insert into attendance_point(attendance, point) values('"+ap.getAttendance()+"', "+ap.getPoint()+")";
    	jdbcTemplate.execute(sql);
    	return "redirect:/AttendPointSetting";
	}
    
    @RequestMapping("/AttendanceRegistrationScreen")
	public ModelAndView AttendanceRegistrationScreen(ModelAndView m) {
    	var names = jdbcTemplate.queryForList("select name from member");
    	var status = jdbcTemplate.queryForList("select name from member");
    	
    	m.addObject("names",names);
    	m.addObject("status",status);
		m.setViewName("AttendanceRegistrationScreen");
		return m;
	}
    @PostMapping("/AttendanceRegister")
	public void AttendanceRegister(@ModelAttribute DateNameStatus dns) {
    	int id = jdbcTemplate.queryForObject(
				"select id from member where name = '"
				+ dns.getName()+"'",int.class);
    	jdbcTemplate.execute("insert into attendance_status(id, year, month, day, attendance) "
    			+ "values("+id+","+dns.getYear()+","+dns.getMonth()+","+dns.getDay()+","+dns.getState()+")");
	}
}
