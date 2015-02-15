package com.aliyesilkanat.stalker.util;

import java.util.Calendar;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;

public class XSDDateTimeUtil {

	/**
	 * Prepares {@link XSDDateTime} instance from given datetime.s
	 * 
	 * @param dateTime
	 *            given dateTime.
	 * @return created {@link XSDDateTime} instance.
	 */
	public static XSDDateTime convert2XsdDate(String dateTime) {
		// get calendar instance..
		Calendar calendar = Calendar.getInstance();
		// parse date time..
		String[] dateTimeArray = dateTime.split("\\s");
		String date = dateTimeArray[0];
		String time = dateTimeArray[1];
		int firstPoint = date.indexOf(".");
		int secondPoint = date.lastIndexOf(".");
		int timeSplitter = time.indexOf(":");
		// create new time..
		calendar.set(Calendar.MILLISECOND, 00);
		calendar.set(Integer.valueOf(getYear(date, secondPoint)),
				Integer.valueOf(getMonth(date, firstPoint, secondPoint)) - 1,
				Integer.valueOf(getDay(date, firstPoint)),
				Integer.valueOf(getHour(time, timeSplitter)) + 3,
				Integer.valueOf(getMinute(time, timeSplitter)), 0);
		XSDDateTime xsdDateTime = new XSDDateTime(calendar);
		return xsdDateTime;
	}

	public static String getYear(String date, int secondPoint) {
		return date.substring(secondPoint + 1);
	}

	public static String getMonth(String date, int firstPoint, int secondPoint) {
		return date.substring(firstPoint + 1, secondPoint);
	}

	public static String getDay(String date, int firstPoint) {
		return date.substring(0, firstPoint);
	}

	public static String getHour(String date, int splitter) {
		return date.substring(0, splitter);
	}

	public static String getMinute(String date, int splitter) {
		return date.substring(splitter + 1);
	}

}
