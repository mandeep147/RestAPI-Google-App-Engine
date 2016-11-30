package edu.mandeep.cmpe281.controllers;

import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

@Controller
@RequestMapping(value = "/logs")
public class Logs {


	@RequestMapping(method = RequestMethod.GET)

	public ResponseEntity<String> getLogs() {


		String allLogs = "";

		int limit = 10000;

		LogQuery query = LogQuery.Builder.withDefaults();

		query.includeAppLogs(true);

		int count = 0;

		for (RequestLogs record : LogServiceFactory.getLogService().fetch(query)) {

			String log = "\n";

			log += "REQUEST LOG\n";

			DateTime reqTime = new DateTime(record.getStartTimeUsec() / 1000);

			log += "IP: " + record.getIp() + "\n";

			log += "Method: " + record.getMethod() + "\n";

			log += "Resource " + record.getResource() + "\n";

			log += String.format("\nDate: %s", reqTime.toString());



			allLogs += log;

			// Display all the app logs for each request log.

			for (AppLogLine appLog : record.getAppLogLines()) {

				String appLogLine = "\n";

				appLogLine += "\t" + "APPLICATION LOG" + "\n";

				DateTime appTime = new DateTime(appLog.getTimeUsec() / 1000);

				appLogLine += String.format("\n\tDate: %s", appTime.toString());

				appLogLine += "\n\tLevel: " + appLog.getLogLevel() + "";

				appLogLine += "\n\tMessage: " + appLog.getLogMessage() + "\n";

				allLogs += appLogLine;

			}



			if (++count >= limit) {

				break;

			}

		} 

		return new ResponseEntity<String>(allLogs, HttpStatus.OK);

	}

}