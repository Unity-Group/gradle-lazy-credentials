package pl.unity.gradle.plugin.lazycredentials;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.gradle.api.Project;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class PasswordPrompt {
	
	static Credentials promptForCredentials(Project project, String passwordProperty, String usernameProperty) {
		return promptForCredentials(project, passwordProperty, usernameProperty, true);
	}
	
	static Credentials promptForCredentials(Project project, String passwordProperty) {
		return promptForCredentials(project, passwordProperty, null, false);
	}
	
	private static Credentials promptForCredentials(Project project, String passwordProperty, String usernameProperty, boolean askForUsername) {
		Credentials.CredentialsBuilder builder = Credentials.builder();
		
		if (!askForUsername && hasProperty(project, passwordProperty)) {
			builder.password(getProperty(project, passwordProperty).toString());
			return builder.build();
		}
		
		if (hasProperty(project, passwordProperty) && hasProperty(project, usernameProperty)) {
			builder.password(getProperty(project, passwordProperty).toString());
			builder.username(getProperty(project, usernameProperty).toString());
			return builder.build();
		}
		
		if (askForUsername && hasProperty(project, usernameProperty)) {
			askForUsername = false;
			builder.username(getProperty(project, usernameProperty).toString());
		}
		
		if (System.console() != null) {
			if (askForUsername) {
				String username = System.console().readLine("\nPlease input username: ");
				setProperty(project, usernameProperty, username);
				builder.username(username);
			}
			String pass = new String(System.console().readPassword("\nPlease input password: "));
			setProperty(project, passwordProperty, pass);
			builder.password(pass);
			return builder.build();
		}
		else {
			log.info("No console found... Trying to display prompt.");
		}

		if (!java.awt.GraphicsEnvironment.isHeadless()) {
			
			String title = "Gradle - password prompt";
			
			final JDialog dialog = new JDialog();
			dialog.setTitle(title);
			dialog.setAlwaysOnTop(true);
			dialog.setResizable(false);
			dialog.setLocationRelativeTo(null);
			dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
			
			
			JTextField usernameField = new JTextField();
			if (askForUsername) {
				dialog.add(new JLabel("Username:"));
				dialog.add(usernameField);
			}
			
			JPasswordField passwordField = new JPasswordField();
			passwordField.setPreferredSize(new Dimension(150, 25));
			dialog.add(new JLabel("Password:"));
			dialog.add(passwordField);
			
			
			JButton button = new JButton("OK");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.setVisible(false);
					dialog.dispose();
				}
			});
			dialog.add(button);
			dialog.getRootPane().setDefaultButton(button); // so you can use enter to submit form
			
			dialog.setVisible(true);
			dialog.pack();
			
			while (dialog.isVisible() && !Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					dialog.setVisible(false);
					dialog.dispose();
					break;
				}
			}
			
			String pass = new String(passwordField.getPassword());
			builder.password(pass);
			setProperty(project, passwordProperty, pass);
			
			if (askForUsername) {
				String username = usernameField.getText();
				builder.username(username);
				setProperty(project, usernameProperty, username);
			}
			
			return builder.build();
		}
		else {
			log.info("Running headless... Can't create prompt.");
		}

		throw new IllegalArgumentException("No password provided, you can provide it by setting $passwordProperty property.");
	}
	
	private static boolean hasProperty(Project project, String property) {
		Project current = project;
		do {
			if (current.getExtensions().getExtraProperties().has(property)) {
				return true;
			}
			current = current.getParent();
		} while (current != null);
		
		return false;
	}
	
	private static Object getProperty(Project project, String property) {
		Project current = project;
		do {
			if (current.getExtensions().getExtraProperties().has(property)) {
				return current.getExtensions().getExtraProperties().get(property);
			}
			current = current.getParent();
		} while (current != null);
		
		throw new IllegalArgumentException("No property " + property + " found");
	}
	
	private static void setProperty(Project project, String property, Object value) {
		Project current = project;
		while (current.getParent() != null) {
			current = current.getParent();
		}
		
		current.getExtensions().getExtraProperties().set(property, value);
	}
	
	@Data
	@Builder
	public static class Credentials {
		private String username, password;
	}
}