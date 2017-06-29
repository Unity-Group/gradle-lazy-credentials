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
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.gradle.util.Configurable;

import groovy.lang.Closure;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LazyCredentialsExtension implements Configurable<LazyCredentialsExtension> {
	private final Project project;
	
	@Override
	public LazyCredentialsExtension configure(Closure c) {
		if (!Closure.class.isInstance(c.getDelegate()) ||
			DefaultMavenArtifactRepository.class.isInstance(((Closure)c.getDelegate()))) {
			throw new IllegalStateException("This extension can only be used in maven repository config.");
		}
		
		LazyCredentials credentials = new LazyCredentials(project);
		
		Object originalDelegate = ((Closure)c.getDelegate()).getDelegate();
		c.setResolveStrategy(Closure.DELEGATE_FIRST);
		c.setDelegate(credentials);
		c.call();
		
		((DefaultMavenArtifactRepository) originalDelegate).setConfiguredCredentials(credentials);
		return this;
	}
}
