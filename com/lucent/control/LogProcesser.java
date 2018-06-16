package com.lucent.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lucent.gui.CaseResult;

public class LogProcesser {
	private XController controller;
	private String reportName;
	private final File reportFile;
	public LogProcesser(XController controller) {
		this.setController(controller);
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd_HHmmss");
		reportName = df.format(new Date()) + "_running_report.html";
		reportFile = new File(controller.getProject() + "/result/" + reportName);
		
		try {
			initializeReport();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initializeReport() throws IOException {
		File template = new File(System.getProperty("user.home") + "/.sunit/report_template.html");
		
		Document doc = Jsoup.parse(template, "UTF-8");
		// update title
		Element title = doc.selectFirst("title");
		title.text("Test Result for " + reportName);
				
		// update panel head
		Element a = doc.select("a").first();
		a.attr("href", reportName);
		a.text(reportName);
				
		PrintWriter writer = new PrintWriter(
				new BufferedWriter(
						new FileWriter(reportFile)));
				
		writer.println(doc);
		writer.close();
	}
	
	public static Element genTableTemplate() {
		Element divFade = new Element("div");
		Element divDialog = new Element("div");
		Element divContent = new Element("div");
		Element divHeader = new Element("div");
		Element divBody = new Element("div");
		
		Element table = new Element("table");
		table.addClass("table table-hover table-condensed modal-table");
		
		Element tHead = new Element("thead");
		Element trHead = new Element("tr");
		trHead.addClass("success");
		trHead.appendChild(new Element("th").text("#"));
		trHead.appendChild(new Element("th").text("Level"));
		trHead.appendChild(new Element("th").text("Task"));
		trHead.appendChild(new Element("th").text("Message"));
		
		divFade.appendChild(divDialog);
		divDialog.appendChild(divContent);
		divContent.appendChild(divHeader);
		divContent.appendChild(divBody);
		divBody.appendChild(table);
		table.appendChild(tHead);
		table.appendChild(new Element("tbody"));
		tHead.appendChild(trHead);
		
		divFade.addClass("modal fade");
		divFade.attr("id", "");
		divFade.attr("tabindex", "-1");
		divFade.attr("role", "dialog");
		divFade.attr("aria-hidden", "true");
		
		divDialog.addClass("modal-dialog modal-lg");
		divContent.addClass("modal-content");
		divHeader.addClass("modal-header");
		
		Element btnClose = new Element("button");
		btnClose.attr("type", "button");
		btnClose.addClass("close");
		btnClose.attr("data-dismiss", "modal");
		btnClose.attr("aria-label", "Close");
		Element spanClose = new Element("span").text("×");
		spanClose.attr("aria-hidden", "true");
		btnClose.appendChild(spanClose);
		divHeader.appendChild(btnClose);
		
		
		divBody.addClass("modal-body");
		
		return divFade;
	}
	
	// this function will return a <td> includes two tables
	public static Element genTdReport(String fileName) throws Exception {
		Element tableError = genTableTemplate();
		Element tableInfo = genTableTemplate();
		
		Element tbodyError = tableError.select("tbody").first();
		Element tbodyInfo = tableInfo.select("tbody").first();
		
		File log = new File(fileName);
		String logName = log.getName();
		int start = logName.indexOf("@");
		int end = logName.indexOf(".log");
		String id = logName.substring(start + 1, end);
		BufferedReader br = new BufferedReader(new FileReader(log));
		String line = null;
		String result = null;
		List<String> debuglogList = new ArrayList<String>();
		int errCount = 0, infoCount = 0;
		while ((line = br.readLine()) != null) {
			infoCount++;
			JSONObject root = new JSONObject(line);
			String level = root.getString("levelname");
			String task = root.getString("id");
			int index = task.indexOf(".");
			if (index > 0) {
				task = task.substring(index + 1);
			} else {
				task = "#";
			}
			Object message = root.get("message");
			String funcName = root.getString("funcName");
			if (funcName.equals("stop")) {
				result = ((JSONObject) message).getString("state");
			}
			
			if (funcName.equals("parse_log") && message instanceof JSONObject) {
				debuglogList.add(((JSONObject) message).getString("display_name"));
			}
			Element tr = new Element("tr");
			tr.appendChild(new Element("td").text(String.valueOf(infoCount)));
			tr.appendChild(new Element("td").text(level));
			tr.appendChild(new Element("td").text(task));
			tr.appendChild(new Element("td").text(message.toString()));
			tr.addClass("");
			
			tbodyInfo.appendChild(tr);
			
			if (level.equals("ERROR")) {
				errCount++;
				Element errTr = tr.clone();
				errTr.getElementsByTag("td").first().text(String.valueOf(errCount));
				tbodyError.appendChild(errTr);
			}
		}
		br.close();
		
		tableError.attr("id", "id-" + id + "_error");
		tableInfo.attr("id", "id-" + id + "_info");
		
		Element btnErr = new Element("button").text("Error");
		btnErr.addClass("btn btn-xs btn-danger");
		btnErr.attr("data-toggle", "modal");
		btnErr.attr("data-target", "#id-" + id + "_error");
		btnErr.appendChild(new Element("span").text(String.valueOf(errCount)).addClass("badge"));
		if (errCount == 0) {
			btnErr.addClass("disabled");
		}
		
		Element btnInfo = new Element("button").text("Info");
		btnInfo.addClass("btn btn-xs btn-success");;
		btnInfo.attr("data-toggle", "modal");
		btnInfo.attr("data-target", "#id-" + id + "_info");
		btnInfo.appendChild(new Element("span").text(String.valueOf(infoCount)).addClass("badge"));
		
		Element td = new Element("td").text(result);
		td.appendChild(btnErr);
		td.appendChild(tableError);
		td.appendChild(btnInfo);
		td.appendChild(tableInfo);
		
		for (String debuglog : debuglogList) {
			Element link = new Element("a").text(debuglog);
			link.addClass("btn btn-xs btn-primary");
			link.attr("href", debuglog + ".log");
			link.attr("target", "_blank");
			td.appendChild(link);
		}
		
		return td;
	}
	public static int getDuration(String fileName) {
		int duration = -1;
		double startTime = -1.0;
		double endTime = -1.0;
		File log = new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(log));
			String line = null;
			while ((line = br.readLine()) != null) {
				JSONObject root = new JSONObject(line);
				endTime = root.getDouble("created");
				if (startTime < 0) {
					startTime = endTime;
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return -1;
		}
		
		duration = (int) (endTime - startTime);
		return duration;
	}
	
	public static Element genTrReport(String fileName, int seqNo) throws Exception {
		int start = fileName.indexOf("@");
		int end = fileName.indexOf(".log");
		String id = fileName.substring(start, end);
		int duration = getDuration(fileName);
		
		Element tr = new Element("tr");
		tr.appendChild(new Element("td").text(String.valueOf(seqNo)));
		tr.appendChild(new Element("td").text(id));
		tr.appendChild(new Element("td").text(String.valueOf(duration)));
		Element td = genTdReport(fileName);
		tr.appendChild(td);
		
		if (td.text().startsWith("FAILURE")) {
			tr.addClass("danger");
		} else {
			tr.addClass("");
		}
		
		tr.appendChild(new Element("td").text(id));
		return tr;
	}
	
	public void parseResult(String log) {
		File logFile = new File(log);
		Document doc = null;
		try {
			doc = Jsoup.parse(reportFile, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		Element overViewTable = doc.getElementsByTag("table").first();
		Element tableBody = overViewTable.getElementsByTag("tbody").first();
		
		//int size = tableBody.childNodeSize();
		int size = tableBody.children().size();
		
		Element tr = null;
		try {
			//tr = genTrReport(logFile.getAbsolutePath(), 1 + (size - 1) / 2);
			tr = genTrReport(logFile.getAbsolutePath(), 1 + size);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (tr != null) {
			tableBody.appendChild(tr);
		}
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(reportFile)));
			writer.println(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}
	
	public CaseResult getCaseResult(String log) {
		CaseResult result = null;
		File logFile = new File(log);
		String fileName = logFile.getName();
		int start = fileName.indexOf("@");
		int end = fileName.indexOf(".log");
		String id = fileName.substring(start, end);

		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new FileReader(log));
		
			String line = null;
			StringBuilder errorMessage = new StringBuilder();

			while ((line = br.readLine()) != null) {
				JSONObject root = new JSONObject(line);
				String funcName = root.getString("funcName");
				if (funcName.equals("stop") && id.equals(root.getString("id"))) {
					JSONObject message = root.getJSONObject("message");
					String res = message.getString("state");
					result = CaseResult.valueOf(res);
					result.setMessage(message.toString());
				}
				
				String level = root.getString("levelname");
				if (level.equals("ERROR") && 
						(funcName.equals("check_hungcall") || 
								funcName.equals("compare_result"))) {
					errorMessage.append(root.getString("message"));
					errorMessage.append(System.getProperty("line.separator"));
				}
			}
			
			if (result.getValue() == CaseResult.FAILURE) {
				result.setMessage(errorMessage.toString());
			}
			
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result.setMessage(e.getMessage());
			e.printStackTrace();
		} 
		
		result.setLogFile(log);
		result.setReportFile(log.replaceAll(".log$", ".html"));
		result.setTime(getDuration(log));
		return result;
	}

	public XController getController() {
		return controller;
	}

	public void setController(XController controller) {
		this.controller = controller;
	}

	public String getReportName() {
		return reportFile.getAbsolutePath();
	}

	public void calcStat() {
		Document doc = null;
		try {
			doc = Jsoup.parse(reportFile, "utf-8");
		} catch (IOException exception) {
			return;
		}

		/* count pass and fail, one case one tr, 
		 * fail case's tr has class "danger" */
		Element tableBody = doc.getElementsByTag("tbody").first();
		int sum = tableBody.children().size();
		int fail = tableBody.getElementsByClass("danger").size();
		int pass = sum - fail;
		float passRate = sum > 0 ? ((float) pass) / ((float) sum) : 0.0f;

		/* set statistic data:
		 * pass rate (pass)
		 * fail rate (fail)
		 * sum
		 */
		Element legend = doc.getElementsByClass("legend").first();
		Elements spans = legend.getElementsByTag("span");
		spans.get(0).text(String.format("%.2f%% (%d)", passRate * 100, pass));
		spans.get(1).text(String.format("%.2f%% (%d)", (1 - passRate) * 100, fail));
		spans.get(2).text(String.valueOf(sum));

		/** prepare to draw the pie */
		DecimalFormat decimalFormat=new DecimalFormat(".00");
		String passDeg = decimalFormat.format(passRate * 360);
		String failDeg = decimalFormat.format((1 - passRate) * 360);

		/* set rotate degree */
		Element style = doc.getElementsByTag("style").first();
		String css = style.data();
		style.text(String.format(css, passDeg, passDeg, passDeg, passDeg,
				passDeg, passDeg, passDeg, passDeg,
				failDeg, failDeg, failDeg, failDeg));

		Element pieFill = new Element("div").addClass("pie fill");				
		String osId = pass > fail ? "os-success" : "os-fail";
		Element pie = doc.getElementById(osId);
		pie.addClass("gt50");
		pie.appendChild(pieFill);
		
		/* write report file */
		try {
			PrintWriter writer = new PrintWriter(
				new BufferedWriter(
						new FileWriter(reportFile)));
			writer.println(doc);
			writer.close();
		} catch (IOException e) {
			return;
		}		
	}
	
}
