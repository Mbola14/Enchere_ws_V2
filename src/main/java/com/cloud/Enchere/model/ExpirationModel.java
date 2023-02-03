package com.cloud.Enchere.model;

public class ExpirationModel {
    private int day;
    private int hr;
    private int min;
    private int sec;

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getHr() {
		return hr;
	}

	public void setHr(int hr) {
		this.hr = hr;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

    public void toHours() {
        day *= 24;
        min /= 60;
        sec /= 3600;
        hr += day + min + sec;
    }
}
