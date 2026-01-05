package utils;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource; // ✅ CORRECT
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions.Transport;

public class EmailUtil {

	private static final Logger logger = LogManager.getLogger(EmailUtil.class);

	/**
	 * Send email with test results
	 */
	public static void sendEmailWithReport(String subject, String body, String attachmentPath) {

		// Email configuration from config.properties
		String host = ConfigReader.getProperty("email.host", "smtp.gmail.com");
		String port = ConfigReader.getProperty("email.port", "587");
		String from = ConfigReader.getProperty("email.from");
		String password = ConfigReader.getProperty("email.password");
		String to = ConfigReader.getProperty("email.to");

		if (from == null || password == null || to == null) {
			logger.warn("Email configuration not found. Skipping email notification.");
			return;
		}

		logger.info("Preparing to send email to: " + to);

		// Setup mail server properties
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.ssl.trust", host);

		// Create session with authentication
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});

		try {
			// Create message
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);

			// Create multipart message for text and attachment
			Multipart multipart = new MimeMultipart();

			// Add text body
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(body, "text/html");
			multipart.addBodyPart(messageBodyPart);

			// Add attachment if exists
			if (attachmentPath != null && new File(attachmentPath).exists()) {
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(attachmentPath);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(new File(attachmentPath).getName());
				multipart.addBodyPart(messageBodyPart);
				logger.info("Attachment added: " + attachmentPath);
			}

			// Set content
			message.setContent(multipart);

			// Send email
			Transport.send(message);
			logger.info("Email sent successfully to: " + to);

		} catch (MessagingException e) {
			logger.error("Failed to send email: " + e.getMessage(), e);
		}
	}

	/**
	 * Create HTML email body with test results
	 */
	public static String createTestResultEmailBody(int totalTests, int passed, int failed, int skipped,
			String allureUrl) {
		double passPercentage = (totalTests > 0) ? (passed * 100.0 / totalTests) : 0;

		String color = passed == totalTests ? "#28a745" : "#dc3545";

		return "<html><body style='font-family: Arial, sans-serif;'>"
				+ "<h2 style='color: #333;'>OpenEMR Test Execution Report</h2>"
				+ "<hr style='border: 1px solid #ddd;'/>"
				+ "<table style='border-collapse: collapse; width: 100%; max-width: 600px;'>"
				+ "<tr style='background-color: #f8f9fa;'>"
				+ "<td style='padding: 10px; border: 1px solid #ddd;'><b>Total Tests</b></td>"
				+ "<td style='padding: 10px; border: 1px solid #ddd;'>" + totalTests + "</td>" + "</tr>" + "<tr>"
				+ "<td style='padding: 10px; border: 1px solid #ddd;'><b>Passed</b></td>"
				+ "<td style='padding: 10px; border: 1px solid #ddd; color: #28a745;'><b>" + passed + "</b></td>"
				+ "</tr>" + "<tr style='background-color: #f8f9fa;'>"
				+ "<td style='padding: 10px; border: 1px solid #ddd;'><b>Failed</b></td>"
				+ "<td style='padding: 10px; border: 1px solid #ddd; color: #dc3545;'><b>" + failed + "</b></td>"
				+ "</tr>" + "<tr>" + "<td style='padding: 10px; border: 1px solid #ddd;'><b>Skipped</b></td>"
				+ "<td style='padding: 10px; border: 1px solid #ddd; color: #ffc107;'><b>" + skipped + "</b></td>"
				+ "</tr>" + "<tr style='background-color: #f8f9fa;'>"
				+ "<td style='padding: 10px; border: 1px solid #ddd;'><b>Pass Percentage</b></td>"
				+ "<td style='padding: 10px; border: 1px solid #ddd; color: " + color + ";'><b>"
				+ String.format("%.2f", passPercentage) + "%</b></td>" + "</tr>" + "</table>" + "<br/>"

				+ "<p style='color: #666;'><b>Please check the attached report for detailed results.</b> <a href='"
				+ allureUrl + "'>Click to view</a></p>"
				+ "<p style='color: #999; font-size: 12px;'>This is an automated email from OpenEMR Test Automation Framework.</p>"
				+ "</body></html>";
	}
}