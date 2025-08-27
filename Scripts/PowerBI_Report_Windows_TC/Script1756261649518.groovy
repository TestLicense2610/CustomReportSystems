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
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import java.io.File as File
import java.io.IOException as IOException
import java.util.ArrayList as ArrayList
import java.util.List as List

WebUI.delay(5)

// --- Cấu hình các biến ---
// Đặt các biến cấu hình trực tiếp trong script Katalon
String user = 'linh.nguyen@katalon.com'

String password = ''

String workspaceName = 'My workspace'

String datasetName = 'ks_powerbi'

// --- Bắt đầu quá trình ---
KeywordUtil.logInfo('PowerBI Report Processor: Bắt đầu tìm và mở báo cáo PowerBI...')

// ... (phần code của bạn để tìm thư mục báo cáo, không thay đổi) ...
// 1. Gộp tất cả các file JUnit XML và tạo report
// (Bạn có thể bỏ qua bước này nếu bạn đã xuất file CSV trực tiếp từ Katalon)
// ...
// 2. Xây dựng chuỗi lệnh PowerShell
KeywordUtil.logInfo('PowerBI Report Processor: Xây dựng lệnh PowerShell để làm mới báo cáo Power BI...')

// Thay thế đường dẫn này bằng đường dẫn tới thư mục Power BI của bạn nếu cần
String powershellExecutable = 'powershell.exe'

String os = System.getProperty('os.name').toLowerCase()

List<String> command = new ArrayList<String>()

if (os.contains('win')) {
    // Xây dựng một chuỗi lệnh PowerShell duy nhất
	String psCommandString = """ Import-Module MicrosoftPowerBIMgmt.Data -Force; Import-Module MicrosoftPowerBIMgmt.Profile -Force; ${'$'}credential = New-Object System.Management.Automation.PSCredential('${user}', ('${password}' | ConvertTo-SecureString -AsPlainText -Force)); Connect-PowerBIServiceAccount -Credential ${'$'}credential; ${'$'}workspace = Get-PowerBIWorkspace -Name '${workspaceName}'; ${'$'}dataset = Get-PowerBIDataset -Name '${datasetName}'; Start-PowerBIDatasetRefresh -Id ${'$'}dataset.Id -WorkspaceId ${'$'}workspace.Id """

    command.add('powershell.exe')

    command.add('-Command')

    command.add(psCommandString) // macOS/Linux
    // Lệnh này không thay đổi
} else {
    KeywordUtil.markFailed('Allure Report Processor: Tự động làm mới Power BI không được hỗ trợ trên OS này.')

    return null
}

KeywordUtil.logInfo('Allure Report Processor: Lệnh thực thi: ' + command)

// 3. Thực thi lệnh
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
        KeywordUtil.logInfo('Power BI report refresh command executed successfully.')
    } else {
        KeywordUtil.logInfo('Power BI refresh command failed with exit code: ' + exitCode)
    }
}
catch (IOException e) {
    KeywordUtil.markFailed('Allure Report Processor: Lỗi khi thực thi lệnh PowerShell: ' + e.getMessage())
} 
catch (InterruptedException e) {
    KeywordUtil.markFailed('Allure Report Processor: Lỗi khi thực thi lệnh PowerShell: ' + e.getMessage())
} 

KeywordUtil.logInfo('Allure Report Processor: Quá trình làm mới báo cáo Power BI kết thúc.')