package com.lucent.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lucent.gui.CaseIterator;
import com.lucent.gui.CaseResult;
import com.lucent.gui.MainGui;
import com.lucent.model.SurepayCase;

public class XController {
	public static final String[] URL_LIST =	{
		"node_url", 
		"member_node_url", 
		"group_node_url", 
		"scp_url", 
		"uc_lab", 
		"route_url", 
		"test_url", 
		"group_url"};
	public static final String[] BP_LIST = {
		"epay_bp",
		"audit_bp",
		"ectrl_bp",
		"ezone_bp",
		"gprscc_bp",
		"eppsm_bp",
		"drouter_bp"};
	public static final String[] VAR_LIST = {
		"user",
		"user_name"
	};
	private String sunit = "/root/sunit/lx/SUnit3.x";
	private String sunitConfig = "";
	
	private MainGui mainGui;
	private String project;
	
	/* 
	 * stage is got from GUI, it determines case directory
	 * means ar, ar_server, dft, dft_server, ut, ut_server
	 */
	private String stage;
	
	private Map<String, String> urls;
	private Map<String, String> bpLocation;
	private Map<String, String> vars;
	
	private boolean isRunningFlag = false;
	private boolean stopButtonClicked = false;

	public XController() {
		project = null;
		urls = new LinkedHashMap<String, String>();
		bpLocation = new LinkedHashMap<String, String>();
		vars = new LinkedHashMap<String, String>();
	}
	
	private void setVar(JSONObject caseVar, Map<String, String> vars) {
		for (Map.Entry<String, String> entry : vars.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				caseVar.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public void setGui(MainGui gui) {
		mainGui = gui;
	}
	
	public MainGui getGui() {
		return mainGui;
	}
	
	public void setProject(String project) {
		this.project = project;
	}
	
	public String getProject() {
		return project;
	}
	
	public void setStage(String stage) {
		this.stage = stage;
	}
	
	public String getStage() {
		return stage;
	}
	
	public void setUrl(String urlName, String url) {
		urls.put(urlName, url);
	}
	
	public void setBpLocation(String bpName, String bpLoc) {
		bpLocation.put(bpName, bpLoc);
	}
	
	public void setVar(String name, String value) {
		vars.put(name, value);
	}
	
	public Map<String, String> getUrls() {
		return urls;
	}
	
	public Map<String, String> getBps() {
		return bpLocation;
	}
	
	public Map<String, String> getVars() {
		return vars;
	}
	
	public String getSunit() {
		return sunit;
	}
	
	public void setSunit(String sunit) {
		this.sunit = sunit;
	}
	
	public void setSunitConfig(String config) {
		sunitConfig = config;
	}
	
	public String getSunitConfig() {
		return sunitConfig;
	}
	
	public void importConfig(File config) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(config));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		
		JSONObject root = new JSONObject(sb.toString());
		if (root.has("project")) {
			project = root.getString("project");
			mainGui.showProject();
		}
		if (root.has("sunit")) {
			sunit = root.getString("sunit");
		}
		if (root.has("sunit_config")) {
			sunitConfig = root.getString("sunit_config");
		}
		
		for (String url : URL_LIST) {
			if (root.has(url)) {
				urls.put(url, root.getString(url));
			}			
		}
		for (String bp : BP_LIST) {
			if (root.has(bp)) {
				bpLocation.put(bp, root.getString(bp));
			}
		}
		for (String var : VAR_LIST) {
			if (root.has(var)) {
				vars.put(var, root.getString(var));
			}
		}
	}
	
	public void exportConfig(File configFile) throws IOException {
		JSONObject root = new JSONObject();
		root.put("sunit", sunit);
		root.put("sunit_config", sunitConfig);
		setVar(root, urls);
		setVar(root, bpLocation);
		setVar(root, vars);
		
		PrintWriter writer = new PrintWriter(
				new FileWriter(configFile));
		writer.print(root.toString(4));
		writer.close();
	}
	
	public boolean needLoadData() {
		return mainGui.needLoadData();
	}
	
	public void startToRunCase() {
		if (project == null || project.isEmpty()) {
			mainGui.showMessageDialog("Please open a project");
			return;
		}
		mainGui.printLog("start to run cases");
		setRunningFlag(true);
		Thread runner = new Thread(new CaseRunner(this));
		runner.start();
	}
	
	public String runCase(SurepayCase c) {
		mainGui.printLog("run case " + c.getTid());
		File json = new File(project + "/"+ stage + "/" + c.getCustomer() 
				+ "/" + c.getTid() + ".json");
		File jsonBak = new File(json.getAbsolutePath() + ".bak");
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new FileReader(json));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "ERROR: " + c.getTid() + ": " + e.getMessage();
		}
		
		JSONObject root = null;
		try {
			root = new JSONObject(sb.toString());
		} catch (JSONException e) {
			//e.printStackTrace();
			return "ERROR: " + c.getTid() + ": " + e.getMessage();
		}
		json.renameTo(jsonBak);
		JSONObject var = root.getJSONObject("_var");
		setVar(var, urls);
		setVar(var, vars);
		
		try {
			PrintWriter writer = new PrintWriter(
					new FileWriter(json));
			writer.println(root.toString(4));
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String resultLog = runSingleJson(json);
		
		if (json.exists()) {
			json.delete();
		}
		jsonBak.renameTo(json);
		
		return resultLog;
	}
	
	public String switchToCustomer(String customer) {
		mainGui.printLog("switch to customer " + customer);
		File json = new File(project + "/ar_server/lib_load_spa_data_rst_spa.json");
		File jsonBak = new File(json.getAbsolutePath() + ".bak");
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new FileReader(json));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		json.renameTo(jsonBak);
		JSONObject root = new JSONObject(sb.toString());
		JSONObject var = root.getJSONObject("_var");
		var.put("spa_base_data", customer);
		setVar(var, urls);
		setVar(var, bpLocation);
		
		try {
			PrintWriter writer = new PrintWriter(
					new FileWriter(json));
			writer.println(root.toString(4));
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String resultLog = runSingleJson(json);
		if (json.exists()) {
			json.delete();
		}
		jsonBak.renameTo(json);
		
		return resultLog;
	}
	
	public boolean isStopButtonClicked() {
		return stopButtonClicked;
	}
	
	public void stopRunningCase() {
		stopButtonClicked = isRunningFlag;
		mainGui.printLog("stop running cases");
	}
	
	public void resetStopButton() {
		stopButtonClicked = false;
	}
	
	public boolean isRunning() {
		return isRunningFlag;
	}
	
	public void setRunningFlag(boolean flag) {
		isRunningFlag = flag;
	}
	
	public void setRunningRow(int row) {
		mainGui.setRunningRow(row);
	}
	
	public String runSingleJson(File jsonFile) {
		mainGui.printLog("File " + jsonFile.getAbsolutePath());
		
		String cmd = sunitConfig.isEmpty() ? sunit : sunit + " -c " + sunitConfig;
		cmd = cmd + " " + project + " " + jsonFile.getAbsolutePath();
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
		InputStream is = p.getInputStream();
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		final StringBuilder resultLog = new StringBuilder();
		Thread t = new Thread(new Runnable() {
			public void run() {
				String line = null;
				try {
					while ((line = br.readLine()) != null) {
						System.out.println(line);
						if (line.endsWith(".html.")) {
							int start = line.indexOf(project);
							resultLog.append(line.substring(start).replaceAll(".html.", ".log"));
						}
					}
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t.start();
		
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (t.isAlive()) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return resultLog.toString();
	}
	
	public void printLog(String s) {
		mainGui.printLog(s);
	}
	
	public boolean getConfirmation(String str) {
		return mainGui.getConfirmation(str);
	}
	
	public void browseHtml(String uri) {
		try {
			mainGui.browseHtml(uri);
		} catch (URISyntaxException | IOException e) {
			mainGui.showMessageDialog("Fail to open " + uri);
		}
	}
	
	public CaseIterator<SurepayCase> guiCaseIterator() {
		return mainGui.iterator();
	}
	
	public SurepayCase getCaseFromGui(int index) {
		return mainGui.getCaseFromTable(index);
	}
	
	public int getTotalCaseNo() {
		return mainGui.getTotalCaseNo();
	}
	
	public boolean isCaseSelected(int index) {
		return mainGui.isCaseSelected(index);
	}
	
	public void setCaseResult(CaseResult result, int i) {
		mainGui.setCaseResult(result, i);
	}
	
	public CaseResult getCaseResult(int row) {
		return mainGui.getCaseResult(row);
	}
	
	public void setCaseTime(int time, int i) {
		mainGui.setCaseTime(time, i);
	}
	
	public void countSelectedResult() {
		mainGui.countSelectedResult();
	}
	
	public void countAllResult() {
		mainGui.countAllResult();
	}
	
	public void setTimeCount(String s) {
		mainGui.setTimeCount(s);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
