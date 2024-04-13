package com.example.demo;

import java.time.LocalDateTime;
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
    	
    	for (var m: memberPoints) {
    		var name=m.name();
    		for (Map<String, Object> e: attList){
    			if (e.get("name").equals(name)){
    				Integer point = jdbcTemplate.queryForObject(
    						"select point from attendance_point where attendance = '"
    						+ e.get("attendance")+"'",int.class);
    				m.addPoint(point);
    			}
    		}
    	}
    	model.addAttribute("table", memberPoints);
    	return "home";
    }
    @RequestMapping("/MemberRegistrationScreen")
	public String MemScreen(ModelAndView m) {
    	return "MemberRegistrationScreen";
	}
    @PostMapping("/memberRegister")
	public String register(@ModelAttribute MemberGrade mg) {
    	String sql= "insert into user values (1, 'Yamada', 'Tokyo')";
		return "MemberRegistrationScreen";
	}
}
