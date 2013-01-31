/**
* 工程名: 	SasMobile
* 文件名: 	PostResult.java
* 创建人:  	ycxiong
* 创建时间: 	2011-7-11 下午02:41:47
* 版权所有：	Copyright (c) 2011 苏州科大恒星信息技术有限公司  
* 文件描述: 描述该文件的作用
* -----------------------------变更记录 ----------------------------- 
* 日期        		变更人      		版本号  		变更描述  
* ------------------------------------------------------------------  
* 2011-7-11     ycxiong   	1.0.0       	first created  
*/
package com.ahmobile.ar.ustc.domain;

/**
 * 提交操作的反馈结果
 * @since SasMobile 1.0.0
 * @version 1.0 2011-7-11
 * @author ycxiong
 */
public class HttpPostResult 
{
	private String result = "1";
	private String desc = "成功";
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
