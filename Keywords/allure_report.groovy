import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import internal.GlobalVariable

public class allure_report {
	@Keyword
	def findJUnitReportFolders(File directory, List<String> foundFolders) {
		if (directory == null || !directory.isDirectory()) {
			return
		}
	
		File[] files = directory.listFiles()
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					// Recursively search in subdirectories
					findJUnitReportFolders(file, foundFolders)
				} else if (file.getName().equals("JUnit_Report.xml")) {
					// If the file is found, add the parent directory's path
					foundFolders.add(file.getParentFile().getAbsolutePath())
				}
			}
		}
	}
}
