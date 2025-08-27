import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.configuration.RunConfiguration
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.Date
import java.util.ArrayList // Thêm import này
import java.util.List // Thêm import này
import java.util.Collections // Thêm import này


// --- Cấu hình các biến ---
String allureExecutablePath = "/opt/homebrew/bin/allure" // Đảm bảo đường dẫn này chính xác

// --- Bắt đầu quá trình ---
KeywordUtil.logInfo("Allure Report Processor: Bắt đầu tìm và mở báo cáo Allure...")

String projectDir = RunConfiguration.getProjectDir()
String reportsBaseDir = projectDir + File.separator + "Reports"

File reportsRootFolder = new File(reportsBaseDir)

if (!reportsRootFolder.exists() || !reportsRootFolder.isDirectory()) {
    KeywordUtil.markFailed("Allure Report Processor: Thư mục Reports không tìm thấy tại: " + reportsBaseDir)
    return
}

File latestJUnitReportFolder = null
long latestTimestampValue = 0L // Đổi tên biến để tránh nhầm lẫn

// Duyệt qua các thư mục báo cáo để tìm folder mới nhất
// Cấu trúc mong đợi: /Reports/[TSC_Name]/[Timestamp]/[TS_Name]/JUnit_Report.xml
// Hoặc: /Reports/[TS_Name]/[Timestamp]/JUnit_Report.xml

List<File> allTimestampFolders = new ArrayList<File>()

// Cấp 1: Các thư mục Test Suite Collection hoặc Test Suite Name
for (File firstLevelFolder : reportsRootFolder.listFiles()) {
    if (firstLevelFolder.isDirectory()) {
        // Cấp 2: Các thư mục Timestamp (ví dụ: 20250721_162623)
        for (File timestampFolder : firstLevelFolder.listFiles()) {
            if (timestampFolder.isDirectory()) {
                allTimestampFolders.add(timestampFolder)
            }
        }
    }
}

// Sắp xếp các thư mục timestamp theo tên để tìm cái mới nhất một cách hiệu quả hơn
// (Groovy/Java sẽ tự sắp xếp theo chuỗi, đảm bảo timestamp mới nhất ở cuối)
Collections.sort(allTimestampFolders, new Comparator<File>() {
    @Override
    int compare(File f1, File f2) {
        return f1.getName().compareTo(f2.getName());
    }
});


// Sau khi sắp xếp, duyệt từ cuối để tìm thư mục có JUnit_Report.xml
for (int i = allTimestampFolders.size() - 1; i >= 0; i--) {
    File timestampFolder = allTimestampFolders.get(i);
    
    // Kiểm tra trực tiếp JUnit_Report.xml trong thư mục timestampFolder
    File junitFileDirect = new File(timestampFolder, "JUnit_Report.xml");
    if (junitFileDirect.exists()) {
        latestJUnitReportFolder = timestampFolder;
        break; // Đã tìm thấy
    }

    // Nếu không có trực tiếp, tìm sâu hơn một cấp (ví dụ: /Timestamp/TS_Name/JUnit_Report.xml)
    for (File deeperFolder : timestampFolder.listFiles()) {
        if (deeperFolder.isDirectory()) {
            File deeperJUnitFile = new File(deeperFolder, "JUnit_Report.xml");
            if (deeperJUnitFile.exists()) {
                latestJUnitReportFolder = deeperFolder; // Lấy thư mục chứa JUnit_Report.xml
                break; // Đã tìm thấy
            }
        }
    }
    if (latestJUnitReportFolder != null) {
        break; // Đã tìm thấy ở cấp sâu hơn, thoát vòng lặp chính
    }
}


if (latestJUnitReportFolder == null || !latestJUnitReportFolder.exists()) {
    KeywordUtil.markFailed("Allure Report Processor: Không tìm thấy thư mục báo cáo JUnit XML mới nhất trong " + reportsBaseDir)
    return
}

String junitReportFolderPath = latestJUnitReportFolder.getAbsolutePath()
KeywordUtil.logInfo("Allure Report Processor: Thư mục báo cáo JUnit mới nhất được tìm thấy: " + junitReportFolderPath)

// 2. Kiểm tra Allure Commandline Tool
File allureExecFile = new File(allureExecutablePath)
if (!allureExecFile.exists() || !allureExecFile.canExecute()) {
    KeywordUtil.markFailed("Allure Report Processor: LỖI: Allure executable không tìm thấy hoặc không có quyền thực thi tại: " + allureExecutablePath + ". Vui lòng kiểm tra lại đường dẫn và quyền hạn.")
    return
}

// 3. Xây dựng lệnh allure serve
List<String> command = new ArrayList<String>()
command.add("bash") // Sử dụng bash để đảm bảo lệnh được thực thi đúng trong shell
command.add("-c")
command.add("\"" + allureExecutablePath + "\" serve \"" + junitReportFolderPath + "\"")

KeywordUtil.logInfo("Allure Report Processor: Lệnh thực thi: " + command)

// 4. Thực thi lệnh trong một tiến trình riêng biệt
try {
    ProcessBuilder pb = new ProcessBuilder(command)
    pb.directory(new File(projectDir)) // Đặt thư mục làm việc là thư mục gốc của dự án Katalon
    pb.inheritIO() // Kế thừa input/output của Katalon console

    Process process = pb.start()
    // Không cần waitFor() nếu bạn muốn Katalon kết thúc ngay lập tức và báo cáo mở trong nền.
    // Nếu bạn muốn Katalon chờ cho đến khi bạn đóng báo cáo trên trình duyệt, hãy uncomment dòng dưới đây:
    // int exitCode = process.waitFor(5, TimeUnit.MINUTES); // Chờ tối đa 5 phút
    // if (exitCode != 0) {
    //     KeywordUtil.logWarning("Allure Report Processor: Lệnh 'allure serve' kết thúc với mã thoát: " + exitCode);
    // } else {
    //     KeywordUtil.logInfo("Allure Report Processor: Lệnh 'allure serve' đã được khởi động.");
    // }

    // Nếu không chờ, chỉ cần log là nó đã được khởi động
    KeywordUtil.logInfo("Allure Report Processor: Lệnh 'allure serve' đã được khởi động trong nền. Kiểm tra trình duyệt của bạn.")

} catch (IOException e) {
    KeywordUtil.markFailed("Allure Report Processor: LỖI khi thực thi lệnh 'allure serve': " + e.getMessage())
} catch (Exception e) { // Bắt các Exception khác (ví dụ: InterruptedException nếu dùng waitFor)
    KeywordUtil.markFailed("Allure Report Processor: Một lỗi không mong muốn xảy ra: " + e.getMessage())
}
KeywordUtil.logInfo("Allure Report Processor: Quá trình tạo và mở báo cáo Allure kết thúc.")