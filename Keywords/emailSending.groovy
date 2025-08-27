import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import javax.mail.internet.*
import javax.activation.*
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import com.kms.katalon.core.configuration.RunConfiguration
import java.io.File
import java.io.FilenameFilter // Rất quan trọng: phải import cái này
import java.util.Arrays
import java.util.Comparator // Rất quan trọng: phải import cái này
import java.util.Optional // Rất quan trọng: phải import cái này


class emailSending {

	@Keyword
	def pdfSending(def sender, def recipient, def password, filePath) {


		String smtpHost = "smtp.gmail.com"
		String smtpPort = "587"


		String senderEmail = sender// replace with your email
		String senderPassword = password // replace with your gmail APPs password


		Properties props = new Properties()
		props.put("mail.smtp.starttls.enable", "true")
		props.put("mail.smtp.host", smtpHost)
		props.put("mail.smtp.port", smtpPort)
		props.put("mail.smtp.auth", "true")


		// Create a session with authentication
		Session session = Session.getInstance(props, new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(senderEmail, senderPassword)
					}
				})


		try {
			// Create the email message
			MimeMessage message = new MimeMessage(session)
			message.setFrom(new InternetAddress(senderEmail))
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient))
			message.setSubject("[Test Suite Collection] Sending PDF Files")


			// Set email body text
			MimeBodyPart messageBodyPart = new MimeBodyPart()
			messageBodyPart.setText("Please refer to the attachments for more details")


			// Attach the Excel report
			MimeBodyPart attachmentPart = new MimeBodyPart()
			//String filePath = System.getProperty("user.dir") + File.separator + 'support.txt'
			DataSource source = new FileDataSource(filePath)
			attachmentPart.setDataHandler(new DataHandler(source))
			attachmentPart.setFileName(new File(filePath).getName())


			// Combine the parts into a multipart
			Multipart multipart = new MimeMultipart()
			multipart.addBodyPart(messageBodyPart)
			multipart.addBodyPart(attachmentPart)


			// Set the content of the message
			message.setContent(multipart)


			// Send the email
			Transport.send(message)
			println("Email sent with attachment successfully.")
		} catch (MessagingException e) {
			e.printStackTrace()
			println("Failed to send email: " + e.getMessage())
		}
	}

	@Keyword
	def getPDFName(String filePath) {

		int lastSlashIndex = filePath.lastIndexOf("/");
		if (lastSlashIndex != -1 && lastSlashIndex < filePath.length() - 1) {
			String lastValue = filePath.substring(lastSlashIndex + 1);
			System.out.println("The last value: " + lastValue);
			String value = lastValue + ".pdf";
			return value.trim();
		} else {
			System.out.println("No valid value to find.");
		}
	}
}