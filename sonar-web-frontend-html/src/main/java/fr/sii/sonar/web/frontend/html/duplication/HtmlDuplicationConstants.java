package fr.sii.sonar.web.frontend.html.duplication;

import fr.sii.sonar.web.frontend.html.HtmlLanguageConstants;

public class HtmlDuplicationConstants extends HtmlLanguageConstants {
	public static final String REPORT_PATH_KEY = "sonar.sii.duplication.html.report.path";
	public static final String FAIL_MISSING_FILE_KEY = "sonar.sii.duplication.html.file.missing.fail";
	public static final String REPORT_PATH_DEFVALUE = "/report/html-duplication.xml";
	public static final String FAIL_MISSING_FILE_DEFVALUE = "true";
	public static final String SUB_CATEGORY = "Duplication";
	
	public String getReportPathKey() {
		return REPORT_PATH_KEY;
	}
	
	public String getMissingFileFailKey() {
		return FAIL_MISSING_FILE_KEY;
	}

}