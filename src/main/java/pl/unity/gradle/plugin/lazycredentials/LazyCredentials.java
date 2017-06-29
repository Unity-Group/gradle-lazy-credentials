/*
* Copyright 2017 Unity S.A.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package pl.unity.gradle.plugin.lazycredentials;

import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.PasswordCredentials;

import lombok.RequiredArgsConstructor;
import pl.unity.gradle.plugin.lazycredentials.GroovyValues.GroovyValue;

@RequiredArgsConstructor
public class LazyCredentials implements PasswordCredentials {
	private GroovyValue<String> passwordProperty;
	private GroovyValue<String> usernameProperty;
	private GroovyValue<String> username;
	private GroovyValue<String> password;
	private final Project project;

	public void passwordProperty(Object passwordProperty) {
		this.passwordProperty = GroovyValues.asGroovyValue(String.class, passwordProperty);
	}
	
	public void usernameProperty(Object usernameProperty) {
		this.usernameProperty = GroovyValues.asGroovyValue(String.class, usernameProperty);
	}
	
	public void setPassword(String password) {
		this.password = GroovyValues.asGroovyValue(String.class, password);
	}
	
	public void setUsername(String username) {
		this.username = GroovyValues.asGroovyValue(String.class, username);
	}
	
	public void password(Object password) {
		this.password = GroovyValues.asGroovyValue(String.class, password);
	}
	
	public void username(Object username) {
		this.username = GroovyValues.asGroovyValue(String.class, username);
	}

	public String getPassword() {
		if (username != null && password != null) {
			return password.getValue();
		} else if (username != null) {
			return PasswordPrompt.promptForCredentials(project, passwordProperty.getValue()).getPassword();
		} else {
			return PasswordPrompt.promptForCredentials(project, passwordProperty.getValue(), usernameProperty.getValue()).getPassword();
		}
	}
	
	public String getUsername() {
		if (username != null) {
			return username.getValue();
		} else {
			return PasswordPrompt.promptForCredentials(project, passwordProperty.getValue(), usernameProperty.getValue()).getUsername();
		}
	}
}