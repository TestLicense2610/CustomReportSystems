import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.configuration.RunConfiguration
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.List

WebUI.delay(10)

// --- Configuration Variables ---
// Set the correct path for your OS
String allureExecutablePath = 'C:\\Users\\admin.katalon\\scoop\\shims\\allure.cmd'
// On Windows, if 'allure' isn't in your PATH, use the full path, e.g., "C:\\Scoop\\apps\\allure\\2.29.0\\bin\\allure.bat"
// On macOS/Linux, it can usually be "allure" if installed via Homebrew or similar.

// --- Start Process ---
KeywordUtil.logInfo("Allure Report Processor: Starting to find and launch Allure report...")

String projectDir = RunConfiguration.getProjectDir()
String reportsBaseDir = projectDir + File.separator + "Reports"

File reportsRootFolder = new File(reportsBaseDir)

if (!reportsRootFolder.exists() || !reportsRootFolder.isDirectory()) {
	KeywordUtil.markFailed("Allure Report Processor: Reports folder not found at: " + reportsBaseDir)
	return
}

// 1. Find all folders containing JUnit_Report.xml
KeywordUtil.logInfo("Allure Report Processor: Recursively scanning directory to find JUnit_Report.xml...")
List<String> junitReportFolders = new ArrayList<String>()
findJUnitReportFolders(reportsRootFolder, junitReportFolders)

if (junitReportFolders.isEmpty()) {
	KeywordUtil.markFailed("Allure Report Processor: No JUnit_Report.xml files found in " + reportsBaseDir)
	return
}

// 2. Build the allure serve command
StringBuilder commandArgsBuilder = new StringBuilder()
for (String folderPath : junitReportFolders) {
	commandArgsBuilder.append(" \"").append(folderPath).append("\"")
}

List<String> command = new ArrayList<String>()
String os = System.getProperty("os.name").toLowerCase()

if (os.contains("win")) {
	command.add("cmd.exe")
	command.add("/c")
	command.add("start")
	command.add(allureExecutablePath)
	command.add("serve")
	// Add all folder paths as arguments
	for (String folderPath : junitReportFolders) {
		command.add(folderPath)
	}
} else {
	// macOS/Linux
	command.add("bash")
	command.add("-c")
	command.add("\"" + allureExecutablePath + "\" serve" + commandArgsBuilder.toString())
}

KeywordUtil.logInfo("Allure Report Processor: Executing command: " + command)

// 3. Execute the command in a separate process
try {
	ProcessBuilder pb = new ProcessBuilder(command)
	pb.directory(new File(projectDir))
	pb.inheritIO()
	
	Process process = pb.start()
	
	KeywordUtil.logInfo("Allure Report Processor: 'allure serve' command started. Check your browser.")

} catch (IOException e) {
	KeywordUtil.markFailed("Allure Report Processor: ERROR executing 'allure serve' command: " + e.getMessage())
} catch (Exception e) {
	KeywordUtil.markFailed("Allure Report Processor: An unexpected error occurred: " + e.getMessage())
}

KeywordUtil.logInfo("Allure Report Processor: Allure report generation process finished.")

/**
 * Recursive method to find all directories containing 'JUnit_Report.xml'.
 * @param directory The starting directory to search from.
 * @param foundFolders A list to add the found directories to.
 */
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

