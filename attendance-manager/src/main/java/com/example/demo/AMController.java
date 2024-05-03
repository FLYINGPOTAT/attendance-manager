package com.example.demo;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AMController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping()
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
    @GetMapping("/MemberRegistrationScreen")
	public ModelAndView memScreen(ModelAndView m) {
    	var members = jdbcTemplate.queryForList("select * from member");   	
    	m.addObject("members",members);
		m.setViewName("memberRegister");
    	return m;
	}
//    @PostMapping("/memberRegister")
//    public void memberRegister(@ModelAttribute MemberGrade mg) {
//        String sql= "INSERT INTO member(name, grade) VALUES(?,?)";
//        System.out.println(""+mg.getName()+mg.getGrade());
//        jdbcTemplate.update(sql,mg.getName(),mg.getGrade());
    @PostMapping("/memberRegister")
	public ModelAndView memberRegister(@ModelAttribute MemberGrade mg, ModelAndView m) {
    	String sql= "insert into member(name, grade) values('"+mg.getName()+"', "+mg.getGrade()+")";
    	//System.out.println(""+mg.getName()+mg.getGrade());
    	jdbcTemplate.execute(sql);
    	return memScreen(m);
	}
    @PostMapping("/deleteMember")
    public ModelAndView deleteMember(ModelAndView m, @RequestParam("id") String id) {
//    	System.out.println(id);
    	jdbcTemplate.execute("set foreign_key_checks = 0");
    	jdbcTemplate.execute("delete from member where id = "+id);
    	jdbcTemplate.execute("set foreign_key_checks = 1");
    	return memScreen(m);
    }
    
    @GetMapping("/AttendPointSetting")
	public String attendPointSettingScreen(Model m) {
    	var attendancePoints = jdbcTemplate.query("select * from attendance_point",
    			(rs,rowNum) -> new AttendancePoint(rs.getString("attendance"),rs.getString("point")));   	
    	m.addAttribute("attendancePoints",attendancePoints);
//		m.setViewName("AttendPointSetting");
		return "AttendPointSetting";
	}
    @PostMapping("/AttendPointSetting")
	public void APSetting(@ModelAttribute AttendancePoint ap, Model m) {
    	var existData=jdbcTemplate.queryForList("select * from attendance_point "
    			+ "where '"+ap.getAttendance()+"' = attendance");
    	if(existData.isEmpty()) {
    		String sql= "insert into attendance_point(attendance, point) values('"+ap.getAttendance()+"', "+ap.getPoint()+")";
        	jdbcTemplate.execute(sql);
        	m.addAttribute("message","保存しました");
    	}else {
    		var data = existData.get(0);
    		String sql= "update attendance_point set point="+ap.getPoint()+" where attendance='"+ap.getAttendance()+"'";
        	jdbcTemplate.execute(sql);
    		m.addAttribute("message","'"+ap.getAttendance()+"'のポイントを変更しました。 "
        	+data.get("point")+"点 >>> "+ap.getPoint()+"点");
    	}
    	var attendancePoints = jdbcTemplate.query("select * from attendance_point",
    			(rs,rowNum) -> new AttendancePoint(rs.getString("attendance"),rs.getString("point")));   	
    	m.addAttribute("attendancePoints",attendancePoints);
	}
    
    @GetMapping("/AttendanceRegistrationScreen")
	public String AttendanceRegistrationScreen(Model m) {
    	var members= jdbcTemplate.queryForList("select * from member");
    	var attendances = jdbcTemplate.queryForList("select attendance from attendance_point");
    	m.addAttribute("members",members);
    	m.addAttribute("attendances",attendances);
//		m.setViewName("AttendanceRegistrationScreen");
//		return m;
    	return "AttendanceRegistrationScreen";
	}
    @PostMapping("/AttendanceRegistrationScreen")
	public void AttendanceRegister(@ModelAttribute DateIdAttendance dia, Model m) {
    	var existData=jdbcTemplate.queryForList("select member.name, attendance_status.year, attendance_status.month, "
    			+ "attendance_status.day, attendance_status.attendance from attendance_status, member "
    			+ "where member.id = attendance_status.id and member.id = "
    			+dia.getId()+" and attendance_status.year = "
    			+dia.getYear()+" and attendance_status.month = "
    			+dia.getMonth()+" and attendance_status.day = "+dia.getDay());
//    	System.out.println(existData);
    	if(existData.isEmpty()) {
	    	jdbcTemplate.execute("insert into attendance_status(id, year, month, day, attendance) "
	    	+ "values("+dia.getId()+","+dia.getYear()+","+dia.getMonth()+","+dia.getDay()+",'"+dia.getAttendance()+"')");
	    	m.addAttribute("message","保存しました");
    	}else {
    		var data = existData.get(0);
    		String sql= "update attendance_status set attendance='"+dia.getAttendance()
    		+"' where id="+dia.getId()+" and year="+data.get("year")+" and month="+data.get("month")
    		+" and day="+data.get("day");
        	jdbcTemplate.execute(sql);
    		String msg=data.get("name")+"の出席状況を変更しました。 "
    		+data.get("year")+"年"+data.get("month")+"月"+data.get("day")+"日: "
    		+data.get("attendance")+" >>> "+dia.getAttendance();
    		
    		m.addAttribute("message",msg);
    	}
    	var members= jdbcTemplate.queryForList("select * from member");
    	var attendances = jdbcTemplate.queryForList("select attendance from attendance_point");
    	m.addAttribute("members",members);
    	m.addAttribute("attendances",attendances);
//    	m.setViewName("AttendanceRegistrationScreen");
//		return m;
//    	return "redirect:/AttendanceRegistrationScreen";
    }
}
