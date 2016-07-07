package com.dttandroid.dttlibrary.model;

import java.io.Serializable;

/**
 * @Author: lufengwen
 * @Date: 2015年9月17日 下午1:01:24
 * @Description:
 */
public class BrowserExtraInfo implements Serializable {
	private static final long serialVersionUID = 8513615343638843560L;
	private String functionName;
	private Serializable extraObj;
	private String url;
	private String title;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public Serializable getExtraObj() {
		return extraObj;
	}

	public void setExtraObj(Serializable extraObj) {
		this.extraObj = extraObj;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
