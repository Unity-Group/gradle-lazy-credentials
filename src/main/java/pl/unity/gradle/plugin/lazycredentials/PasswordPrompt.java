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
		
		if (askForUsername && project.hasProperty(usernameProperty)) {
			askForUsername = false;
			builder.username(project.getExtensions().getExtraProperties().get(usernameProperty).toString());
		}
		
		if (project.hasProperty(passwordProperty) && project.hasProperty(usernameProperty)) {
			builder.password(project.getExtensions().getExtraProperties().get(passwordProperty).toString());
			builder.username(project.getExtensions().getExtraProperties().get(usernameProperty).toString());
			return builder.build();
		}
		
		if (System.console() != null) {
			if (askForUsername) {
				String username = System.console().readLine("\nPlease input username: ");
				project.getExtensions().getExtraProperties().set(usernameProperty, username);
				builder.username(username);
			}
			String pass = new String(System.console().readPassword("\nPlease input password: "));
			project.getExtensions().getExtraProperties().set(passwordProperty, pass);
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
			project.getExtensions().getExtraProperties().set(passwordProperty, pass);
			
			if (askForUsername) {
				String username = usernameField.getText();
				builder.username(username);
				project.getExtensions().getExtraProperties().set(usernameProperty, username);
			}
			
			return builder.build();
		}
		else {
			log.info("Running headless... Can't create prompt.");
		}

		throw new IllegalArgumentException("No password provided, you can provide it by setting $passwordProperty property.");
	}
	
	@Data
	@Builder
	public static class Credentials {
		private String username, password;
	}
}