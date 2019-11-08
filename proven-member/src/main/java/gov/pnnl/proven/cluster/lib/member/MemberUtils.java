/*******************************************************************************
 * Copyright (c) 2017, Battelle Memorial Institute All rights reserved.
 * Battelle Memorial Institute (hereinafter Battelle) hereby grants permission to any person or entity 
 * lawfully obtaining a copy of this software and associated documentation files (hereinafter the 
 * Software) to redistribute and use the Software in source and binary forms, with or without modification. 
 * Such person or entity may use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and may permit others to do so, subject to the following conditions:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 * following disclaimers.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Other than as used herein, neither the name Battelle Memorial Institute or Battelle may be used in any 
 * form whatsoever without the express written consent of Battelle.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * BATTELLE OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * General disclaimer for use with OSS licenses
 * 
 * This material was prepared as an account of work sponsored by an agency of the United States Government. 
 * Neither the United States Government nor the United States Department of Energy, nor Battelle, nor any 
 * of their employees, nor any jurisdiction or organization that has cooperated in the development of these 
 * materials, makes any warranty, express or implied, or assumes any legal liability or responsibility for 
 * the accuracy, completeness, or usefulness or any information, apparatus, product, software, or process 
 * disclosed, or represents that its use would not infringe privately owned rights.
 * 
 * Reference herein to any specific commercial product, process, or service by trade name, trademark, manufacturer, 
 * or otherwise does not necessarily constitute or imply its endorsement, recommendation, or favoring by the United 
 * States Government or any agency thereof, or Battelle Memorial Institute. The views and opinions of authors expressed 
 * herein do not necessarily state or reflect those of the United States Government or any agency thereof.
 * 
 * PACIFIC NORTHWEST NATIONAL LABORATORY operated by BATTELLE for the 
 * UNITED STATES DEPARTMENT OF ENERGY under Contract DE-AC05-76RL01830
 ******************************************************************************/

/*******************************************************************************
 * Copyright (c) 2017, Battelle Memorial Institute All rights reserved.
 * Battelle Memorial Institute (hereinafter Battelle) hereby grants permission to any person or entity 
 * lawfully obtaining a copy of this software and associated documentation files (hereinafter the 
 * Software) to redistribute and use the Software in source and binary forms, with or without modification. 
 * Such person or entity may use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and may permit others to do so, subject to the following conditions:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 * following disclaimers.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Other than as used herein, neither the name Battelle Memorial Institute or Battelle may be used in any 
 * form whatsoever without the express written consent of Battelle.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * BATTELLE OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * General disclaimer for use with OSS licenses
 * 
 * This material was prepared as an account of work sponsored by an agency of the United States Government. 
 * Neither the United States Government nor the United States Department of Energy, nor Battelle, nor any 
 * of their employees, nor any jurisdiction or organization that has cooperated in the development of these 
 * materials, makes any warranty, express or implied, or assumes any legal liability or responsibility for 
 * the accuracy, completeness, or usefulness or any information, apparatus, product, software, or process 
 * disclosed, or represents that its use would not infringe privately owned rights.
 * 
 * Reference herein to any specific commercial product, process, or service by trade name, trademark, manufacturer, 
 * or otherwise does not necessarily constitute or imply its endorsement, recommendation, or favoring by the United 
 * States Government or any agency thereof, or Battelle Memorial Institute. The views and opinions of authors expressed 
 * herein do not necessarily state or reflect those of the United States Government or any agency thereof.
 * 
 * PACIFIC NORTHWEST NATIONAL LABORATORY operated by BATTELLE for the 
 * UNITED STATES DEPARTMENT OF ENERGY under Contract DE-AC05-76RL01830
 ******************************************************************************/

package gov.pnnl.proven.cluster.lib.member;

//import static gov.pnnl.proven.lib.member.utils.Consts.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collection of static methods supporting ProvEn operations
 */
public class MemberUtils {

	private static Logger log = LoggerFactory.getLogger(MemberUtils.class);

	public static final String FILE_SEP = System.getProperty("file.separator");

	/** 
	 * Returns the throwable cause if it exists (i.e. not null) or the throwable name itself.
	 * 
	 * @param t the {@code Throwable}
	 * 
	 * @return simple class name of throwable or it's cause if it exists.
	 */
	public static String exCause(Throwable t) {
		return (null != t.getCause()) ? (t.getCause().getClass().getSimpleName()) : (t.getClass().getSimpleName());
	}

	/**
	 * Finds a resource in class path
	 * 
	 * @param resource
	 *            location of resource
	 * @param clazz
	 *            classloader context
	 * 
	 * @throws IOException
	 *             if resource not found
	 * @return returns a File object if resource found returned
	 */
	public static <T> File getCpResource(String location, Class<T> clazz) throws IOException {

		File ret = null;

		URL resource = clazz.getClassLoader().getResource(location);

		if (null != resource) {
			try {
				ret = new File(resource.toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
				ret = null;
			}
		} else {
			throw new IOException("Resource not found for  : " + location);
		}

		return ret;
	}

	/**
	 * Finds resources in classpath under provided directory location.
	 * 
	 * @param locationDir
	 *            directory location in classpath to retrieve resources from
	 * @param clazz
	 *            classloader context
	 * @return returns list of resources in directory location. An empty list is
	 *         returned if locationDir is not a directory.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static <T> File[] getCpResources(String locationDir, Class<T> clazz) throws URISyntaxException, IOException {

		File[] ret = null;

		Enumeration<URL> en = clazz.getClassLoader().getResources(locationDir);

		if (en.hasMoreElements()) {

			URL locationDirUrl = en.nextElement();

			File dir = new File(locationDirUrl.toURI());

			ret = dir.listFiles();
		}

		return ret;
	}

	public static String getRelPath(String... pathElements) {
		return getPath(true, pathElements);
	}

	public static String getAbsPath(String... pathElements) {
		return getPath(false, pathElements);
	}

	public static String getPath(boolean isRelative, String... pathElements) {

		String ret = (isRelative ? "" : FILE_SEP);

		for (String pathElement : pathElements) {
			if (null != pathElement) {
				ret = ret + pathElement + FILE_SEP;
			}
		}

		return ret;
	}

	public static boolean isLocalResource(URI resource) {

		boolean ret = false;

		if (resource.toString().startsWith("file")) {
			ret = true;
		}

		return ret;
	}

	/**
	 * Gets a TimeZone object for the provided time zone id.
	 * 
	 * @param tzId
	 *            time zone id.
	 * 
	 * @return returns a TimeZone for the provided time zone id. If the time
	 *         zone id is null then the default time zone for this host is
	 *         returned. If the id is invalid then 'GMT' is used.
	 * 
	 */
	public static TimeZone getTimezone(String tzId) {

		TimeZone ret = TimeZone.getDefault();

		if (tzId != null) {
			ret = TimeZone.getTimeZone(tzId);
		}

		return ret;
	}

	/**
	 * Creates a calendar using the provided integer date/time and timezone
	 * values. Format YYYYMMDD[HHMISS]. Must contain year, month, day at a
	 * minimum. Hour, minute, second are optional.
	 * 
	 * @param dtm
	 *            integer representation of a date/time value
	 * @param tzId
	 *            time zone id to use when creating instance of calendar, if
	 *            null local time zone will be used.
	 * @return a calendar for provided data/time and time zone id. If error
	 *         encountered, null will be returned.
	 */
	public static Calendar getCalendarFromLong(Long dtm, String tzId) {

		Calendar ret = null;

		String dtmStr = dtm.toString();
		int year;
		int month;
		int day;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (dtm >= 0) {

			try {
				if (dtmStr.length() >= 8) {

					year = Integer.parseInt(dtmStr.substring(0, 4));
					// Month is zero based
					month = Integer.parseInt(dtmStr.substring(4, 6)) - 1;
					day = Integer.parseInt(dtmStr.substring(6, 8));

					if (dtmStr.length() >= 10) {
						hour = Integer.parseInt(dtmStr.substring(8, 10));
					}

					if (dtmStr.length() >= 12) {
						minute = Integer.parseInt(dtmStr.substring(10, 12));
					}

					if (dtmStr.length() >= 14) {
						second = Integer.parseInt(dtmStr.substring(12, 14));
					}

					ret = Calendar.getInstance(getTimezone(tzId));
					ret.set(year, month, day, hour, minute, second);
				}
			} catch (NumberFormatException e) {
				// log.info("Failed to create calander for value : " + dtmStr,
				// e);
			}

		}

		return ret;

	}

	/**
	 * Creates an integer date/time representation, using the provided Calendar
	 * value. Integer representation of date/time is YYYYMMDDHHMISS. Calendar
	 * must contain year, month, day at a minimum. Hour, minute, second are
	 * optional, and will be set to "00" if not provided.
	 * 
	 * @param cal
	 *            calendar containing date/time value
	 * @return an integer representation of calendar. If error encountered, null
	 *         will be returned.
	 */
	public static Long getLongFromCalendar(Calendar cal) {

		Long ret = null;

		String dtmStr;
		String year;
		String month;
		String day;
		String hour = "00";
		String minute = "00";
		String second = "00";

		if (cal != null) {

			try {
				if ((cal.isSet(Calendar.YEAR)) && (cal.isSet(Calendar.MONTH)) && (cal.isSet(Calendar.DAY_OF_MONTH))) {

					year = String.format("%04d", cal.get(Calendar.YEAR));
					// Month is zero based
					month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
					day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

					if (cal.isSet(Calendar.HOUR_OF_DAY)) {
						hour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY));
					}

					if (cal.isSet(Calendar.MINUTE)) {
						minute = String.format("%02d", cal.get(Calendar.MINUTE));
					}

					if (cal.isSet(Calendar.SECOND)) {
						second = String.format("%02d", cal.get(Calendar.SECOND));
					}

					dtmStr = year + month + day + hour + minute + second;
					ret = Long.parseLong(dtmStr);

				}
			} catch (NumberFormatException e) {
				log.info("Failed to create Long from Calander for value :: " + cal.toString(), e);
			}

		}

		return ret;

	}

	/**
	 * Returns long representation of current time, for specified time zone.
	 * 
	 * @param tzId
	 *            time zone id. If null, local time zone will be used.
	 * @return a Long integer representation of current time. If error
	 *         encountered, null will be returned.
	 */
	public static Long getLongForCurrentTime(String tzId) {
		return getLongFromCalendar(Calendar.getInstance(getTimezone(tzId)));
	}

	/**
	 * Returns long representation of current time for local time zone.
	 * 
	 * @return a Long integer representation of current time. If error
	 *         encountered, null will be returned.
	 */
	public static Long getLongForCurrentTime() {
		return getLongFromCalendar(Calendar.getInstance(getTimezone(null)));
	}

	/**
	 * Returns long representation of current time for local time zone in period
	 * format (YYYYMMDD).
	 * 
	 * @return a Long integer representation of current time in period format
	 *         (YYYMMDD). If error encountered, null will be returned.
	 */
	public static Integer getPeriodForCurrentTime() {
		Calendar calendar = Calendar.getInstance(getTimezone(null));
		String period = getLongFromCalendar(calendar).toString();
		return Integer.valueOf(period.substring(0, 8));
	}

	public static Integer getNextPeriodForCurrentTime() {
		Calendar calendar = Calendar.getInstance(getTimezone(null));
		calendar.add(Calendar.HOUR_OF_DAY, 24);
		String period = getLongFromCalendar(calendar).toString();
		return Integer.valueOf(period.substring(0, 8));
	}

	public static Integer getPreviousPeriodForCurrentTime() {
		Calendar calendar = Calendar.getInstance(getTimezone(null));
		calendar.add(Calendar.HOUR_OF_DAY, -24);
		String period = getLongFromCalendar(calendar).toString();
		return Integer.valueOf(period.substring(0, 8));
	}

	public static Integer getAdjustedPeriodForCurrentTime(int hours) {
		Calendar calendar = Calendar.getInstance(getTimezone(null));
		calendar.add(Calendar.HOUR_OF_DAY, (hours));
		String period = getLongFromCalendar(calendar).toString();
		return Integer.valueOf(period.substring(0, 8));
	}

	public static Integer getPeriodForCurrentTime(String tzId) {
		Calendar calendar = Calendar.getInstance(getTimezone(tzId));
		String period = getLongFromCalendar(calendar).toString();
		return Integer.valueOf(period.substring(0, 8));
	}

	public static Integer getPeriodForCalendar(Calendar calendar) {
		String period = getLongFromCalendar(calendar).toString();
		return Integer.valueOf(period.substring(0, 8));
	}

	public static Integer getAdjustedPeriodForCalendar(Calendar calendar, int hours) {
		calendar.add(Calendar.HOUR_OF_DAY, (hours));
		String period = getLongFromCalendar(calendar).toString();
		return Integer.valueOf(period.substring(0, 8));
	}

	public static boolean isPeriodEffective(Integer period, Integer effectivePeriod, Integer expirePeriod) {

		boolean ret = false;

		if ((period != null) && (effectivePeriod != null)) {
			if (period >= effectivePeriod) {
				if (expirePeriod == null) {
					ret = true;
				} else if (period <= expirePeriod) {
					ret = true;
				}
			}
		}

		return ret;
	}

	/**
	 * Determines begin time of range, provided the ranges duration and end
	 * time.
	 * 
	 * @param end
	 *            integer representation of end time
	 * @param range
	 *            in milliseconds
	 * @param tzId
	 *            time zone id, local time zone is used if null.
	 * @return an integer value date/time representation of the begin time for
	 *         range and end time.
	 */
	public static Long getBeginForRange(long end, int range, String tzId) {

		long endTimeEpoch = getCalendarFromLong(end, tzId).getTimeInMillis();
		Calendar beginCal = Calendar.getInstance(getTimezone(tzId));
		beginCal.setTimeInMillis(endTimeEpoch - range);

		return getLongFromCalendar(beginCal);
	}

	/**
	 * Determines end time of range, provided the ranges duration and begin
	 * time.
	 * 
	 * @param begin
	 *            integer representation of begin time
	 * @param range
	 *            in milliseconds
	 * @param tzId
	 *            time zone id, local time zone is used if null.
	 * @return an integer value date/time representation of the end time for
	 *         range and begin time.
	 */
	public static Long getEndForRange(long begin, int range, String tzId) {

		long beginTimeEpoch = getCalendarFromLong(begin, tzId).getTimeInMillis();
		Calendar endCal = Calendar.getInstance(getTimezone(tzId));
		endCal.setTimeInMillis(beginTimeEpoch + range);

		return getLongFromCalendar(endCal);
	}

	/**
	 * Determines range in millis
	 * 
	 * @param begin
	 *            integer representation of begin time
	 * @param end
	 *            integer representation of end time
	 * @param tzId
	 *            time zone id, local time zone is used if null.
	 * 
	 * @return range in milliseconds. If begin > end, zero is returned.
	 */
	public static Long getRange(long begin, long end, String tzId) {

		long ret = 0;

		long beginTimeEpoch = getCalendarFromLong(begin, tzId).getTimeInMillis();
		long endTimeEpoch = getCalendarFromLong(end, tzId).getTimeInMillis();

		if (endTimeEpoch > beginTimeEpoch) {
			ret = endTimeEpoch - beginTimeEpoch;
		}

		return ret;

	}

	/**
	 * Gets number of millis for the provided number of seconds.
	 * 
	 * @param seconds
	 *            number of seconds
	 * @return number of millis for the provided number of seconds.
	 */
	public static int getMillisFromSeconds(int seconds) {
		return Math.abs(seconds * 1000);
	}

	/**
	 * Gets number of millis for the provided number of minutes.
	 * 
	 * @param minutes
	 *            number of minutes
	 * @return number of millis for the provided number of minutes.
	 */
	public static int getMillisFromMinutes(int minutes) {
		return Math.abs(minutes * 60 * 1000);
	}

	/**
	 * Gets number of millis for the provided number of hours.
	 * 
	 * @param hours
	 *            number of hours
	 * @return number of millis for the provided number of hours.
	 */
	public static int getMillisFromHours(int hours) {
		return Math.abs(hours * 60 * 60 * 1000);
	}

	/**
	 * Gets number of millis for the provided number of days.
	 * 
	 * @param days
	 *            number of days
	 * @return number of millis for the provided number of days.
	 */
	public static int getMillisFromDays(int days) {
		return Math.abs(days * 24 * 60 * 60 * 1000);
	}

	public static int getDaysFromMillis(Long millis) {
		return Math.round(millis / (24 * 60 * 60 * 1000));
	}

	public static int getHoursFromMillis(Long millis) {
		return Math.round(millis / (60 * 60 * 1000));
	}

	public static int getMinutesFromMillis(Long millis) {
		return Math.round(millis / (60 * 1000));
	}

	public static int getSecondsFromMillis(Long millis) {
		return Math.round(millis / (1000));
	}

	/**
	 * Gets Timestamp, for current time.
	 * 
	 * @return a Timestamp, representing the current time.
	 */
	public static Timestamp currentTimestamp() {
		return currentTimestamp(new Date().getTime());
	}

	public static Timestamp currentTimestamp(long epochTime) {
		return new Timestamp(epochTime);
	}

	/**
	 * Gets Timestamp, for provided date/time.
	 * 
	 * @param fromDate
	 *            date/time value to convert to a Timestamp.
	 * @return a Timestamp. representing the provided data/time value.
	 */
	public static Timestamp getTimestamp(Date fromDate) {
		return new Timestamp(fromDate.getTime());
	}

	/**
	 * Gets Timestamp, for provided calendar.
	 * 
	 * @param cal
	 *            calendar to convert to a Timestamp.
	 * @return a Timestamp. representing the provided calendar value.
	 */
	public static Timestamp getTimestamp(Calendar cal) {
		return new Timestamp(cal.getTime().getTime());
	}

	/**
	 * Gets string representation of a file.
	 * 
	 * @param aFile
	 *            the file
	 * @return a string representation of a file.
	 */
	public static String getContents(File aFile) {

		StringBuilder contents = new StringBuilder();

		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(aFile));
			try {
				String line = null; // not declared within while loop
				/*
				 * readLine is a bit quirky : it returns the content of a line
				 * MINUS the newline. it returns null only for the END of the
				 * stream. it returns an empty String if two newlines appear in
				 * a row.
				 */
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return contents.toString();
	}

	/**
	 * Performs safe string quoting for pgsql
	 * 
	 * @param str
	 *            the string to be quoted
	 * @return the quoted string
	 */
	public static String sQuote(String str) {
		String ret;
		ret = str.replace("'", "''");
		ret = "'" + ret + "'";
		return ret;
	}

	/**
	 * Converts all entries in the string list to upper case.
	 * 
	 * @param list
	 *            The list whose values should be upper cased.
	 */
	public static void upperCaseListEntries(List<String> list) {
		if (null == list) {
			return;
		}

		int size = list.size();
		for (int i = 0; i < size; i++) {
			String s = list.get(i);
			if (null != s) {
				list.set(i, s.toUpperCase());
			}
		}
	}

	/**
	 * Performs a JNDI lookup
	 * 
	 * @param name
	 *            jndi lookup name
	 * @return the jndi object if found, null otherwise
	 */
	public static Object lookupJndi(String name) {

		Object ret = null;
		Context context = null;

		try {
			context = new InitialContext();
			ret = context.lookup(name);
		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}

		return ret;
	}

}
