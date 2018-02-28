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


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class PasswordPromptTest {
	private Project project;
	private ExtraPropertiesExtension extraProperties;
	
	@Test
	@Ignore("run manually to check GUI")
	public void shouldShowDialog() {
		// given
		String passwordProperty = "passProp";
		String userNameProperty = "username";
		project.getExtensions().getExtraProperties().set(userNameProperty, "somename");
		
		// when
		PasswordPrompt.Credentials credentials = PasswordPrompt.promptForCredentials(project, passwordProperty, userNameProperty);
		credentials = PasswordPrompt.promptForCredentials(project, passwordProperty, userNameProperty);
		
		// then
		System.out.println(credentials.getUsername()); // since this is run manually lets just look at it...
		System.out.println(credentials.getPassword());
	}
	
	@Before
	public void setUp() {
		project = ProjectBuilder.builder()
			.withParent(ProjectBuilder.builder().build())
			.build();
	}
}