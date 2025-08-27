import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory as CheckpointFactory
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testcase.TestCaseFactory as TestCaseFactory
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository as ObjectRepository
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.annotation.SetUp as SetUp
import com.kms.katalon.core.annotation.SetupTestCase as SetupTestCase
import com.kms.katalon.core.annotation.TearDown as TearDown
import com.kms.katalon.core.annotation.TearDownTestCase as TearDownTestCase
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import java.io.File as File
import java.io.IOException as IOException

// --- Cấu hình các biến ---
// Đặt các biến cấu hình trực tiếp trong script Katalon
// --- Bắt đầu quá trình ---
// ... (phần code của bạn để tìm thư mục báo cáo, không thay đổi) ...
// 1. Gộp tất cả các file JUnit XML và tạo report
// (Bạn có thể bỏ qua bước này nếu bạn đã xuất file CSV trực tiếp từ Katalon)
// ...
// 2. Xây dựng chuỗi lệnh PowerShell
// Thay thế đường dẫn này bằng đường dẫn tới thư mục Power BI của bạn nếu cần
// Xây dựng một chuỗi lệnh PowerShell duy nhất
// macOS/Linux
// Lệnh này không thay đổi
// 3. Thực thi lệnh

@SetUp(skipped = true)
def setUp() {
}

@TearDown(skipped = false)
def tearDown() {
	// --- Cấu hình các biến ---
	String user = "linh.nguyen@katalon.com"
	String password = "Betaodethuong91@!#"
	String workspaceName = "My workspace"
	String datasetName = "ks_powerbi"
	String projectDir = RunConfiguration.getProjectDir()
	String psScriptPath = projectDir + File.separator + "RefreshPowerBI.ps1"
	KeywordUtil.logInfo("PowerBI Report Processor: Bắt đầu làm mới báo cáo PowerBI...")
	// Xây dựng lệnh gọi file PowerShell
	def command = new ArrayList<String>()
	command.add("powershell.exe")
	command.add("-ExecutionPolicy")
	command.add("Bypass") // Bỏ qua chính sách thực thi script
	command.add("-File")
	command.add(psScriptPath)
	command.add("-user")
	command.add(user)
	command.add("-password")
	command.add(password)
	command.add("-workspaceName")
	command.add(workspaceName)
	command.add("-datasetName")
	command.add(datasetName)
	KeywordUtil.logInfo("PowerBI Report Processor: Lệnh thực thi: " + command.join(" "))
	try {
		ProcessBuilder pb = new ProcessBuilder(command)
		pb.directory(new File(projectDir))
		pb.redirectErrorStream(true)
		Process process = pb.start()
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))
		String line
		while ((line = reader.readLine()) != null) {
			KeywordUtil.logInfo(line)
		}
		int exitCode = process.waitFor()
		if (exitCode == 0) {
			KeywordUtil.logInfo("Power BI report refresh command executed successfully.")
		} else {
			KeywordUtil.logInfo("Power BI refresh command failed with exit code: " + exitCode)
		}
	} catch (IOException | InterruptedException e) {
		KeywordUtil.markFailed("PowerBI Report Processor: Lỗi khi thực thi lệnh PowerShell: " + e.getMessage())
	}
	KeywordUtil.logInfo("PowerBI Report Processor: Quá trình làm mới báo cáo Power BI kết thúc.")
}

@SetupTestCase(skipped = true)
def setupTestCase() {
}

@TearDownTestCase(skipped = true)
def tearDownTestCase() {
}

/**
 * References:
 * Groovy tutorial page: http://docs.groovy-lang.org/next/html/documentation/
 */