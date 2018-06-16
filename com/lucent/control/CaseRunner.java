package com.lucent.control;

import com.lucent.gui.CaseResult;
import com.lucent.model.SurepayCase;

class TimeCounter extends Thread {
	private boolean stopFlag;
	private XController controller;
	public TimeCounter(XController c) {
		controller = c;
	}
	public void stopCounting() {
		stopFlag = true;
	}
	public void run() {
		int h = 0, m = 0, s = 0;
		StringBuilder sb = new StringBuilder();
		while (!stopFlag) {
			s++;
			if (s == 60) {
				s = 0;
				m++;
			}
			if (m == 60) {
				m = 0;
				h++;
			}
			sb.delete(0, sb.length());
			if (h < 10) {
				sb.append("0");
			}
			sb.append(h).append(":");
			if (m < 10) {
				sb.append("0");
			}
			sb.append(m).append(":");
			if (s < 10) {
				sb.append("0");
			}
			sb.append(s);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			controller.setTimeCount(sb.toString());
		}
	}
}
public class CaseRunner implements Runnable {
	private XController controller;
	
	public CaseRunner(XController controller) {
		this.controller = controller;
	}
	
	public void run() {
		TimeCounter timeCounter = new TimeCounter(controller);
		timeCounter.start();
		final LogProcesser processer = new LogProcesser(controller);
		
		int sum = controller.getTotalCaseNo();
		int i = 0;
		String previousCustomer = null;
		Thread theLastProcesser = null;
		while (i < sum) {
			if (!controller.isCaseSelected(i)) {
				i++;
				continue;
			}
			if (controller.isStopButtonClicked()) {
				controller.resetStopButton();
				break;
			}
			SurepayCase caseToRun = controller.getCaseFromGui(i);
			controller.setRunningRow(i);
			String cus = caseToRun.getCustomer();
			if ((previousCustomer == null && controller.needLoadData())
					|| ( previousCustomer != null && !cus.equals(previousCustomer))) {
				controller.printLog("Change customer to " + cus);
				previousCustomer = cus;
				controller.switchToCustomer(cus);
			}
			final String resultLog = controller.runCase(caseToRun);
			if (resultLog.startsWith("ERROR")) {
				controller.printLog(resultLog);
				CaseResult caseResult = new CaseResult(CaseResult.ERROR);
				caseResult.setMessage(resultLog);
				controller.setCaseResult(caseResult, i);
			} else {
				controller.printLog("Finish log: " + resultLog);
				final int row = i;
				Thread t = new Thread(new Runnable() {
					public void run() {
						processer.parseResult(resultLog);
						CaseResult caseResult = processer.getCaseResult(resultLog);
						controller.setCaseTime(caseResult.getTimeCost(), row);
						controller.setCaseResult(caseResult, row);
					}
				});
				t.start();
				theLastProcesser = t;
			}
			i++;
		}
		timeCounter.stopCounting();
		if (theLastProcesser != null && theLastProcesser.isAlive()) {
			try {
				theLastProcesser.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		processer.calcStat();
		
		controller.setRunningFlag(false);
		controller.setRunningRow(-1);
		controller.countSelectedResult();
		controller.printLog("Final report: " + processer.getReportName());
		
		if (controller.getConfirmation("Need open final report?")) {
			controller.browseHtml(processer.getReportName());
		}		
	}

}
