package com.xiaoshu.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.xiaoshu.entity.TbGoods;

public class Gvo extends TbGoods{
	private String tname;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date start;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date end;
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}

	
	
	

}
