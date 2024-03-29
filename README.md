Development discontinued
============

In newer gradle versions this plugin no longer works and we are not planning
to fix it. The biggest problem - eager initialization of properties - is 
solved in gradle itself. While it still provides a popup that's useful
when you don't want to store your password in any form we don't feel like 
it's really worth the effort and the hacks it needed!

https://docs.gradle.org/current/samples/sample_publishing_credentials.html


Old docs follow:

Lazy credentials gradle plugin
==============================

This is plugin adds `lazyCredentials` extension that can be used
to replace `credentials` in maven repo definition. Those
credentials won't be evaluated until it's necessary. It can
also prompt user for credentials.

When to use this plugin
-----------------------

It's useful when project uses gradle's `maven-publish` plugin -
you can use properties to pass repo username and password and 
tasks not requiring them won't fail - you only need to
provide them when necessary. This also simplifies config
for team members who don't have to (or are not allowed to)
publish to maven repo - they can completely ignore configuring
credentials.

Furthermore, since plugin can prompt you for password (or username 
and password) you don't have to store password in properties file
or put it as parameter.


How to use this plugin
----------------------

You can find instruction on how to add this plugin on
[gradle plugin portal.](https://plugins.gradle.org/plugin/pl.unity.lazy-credentials)

Basic usage is quite simple:

```
publishing {
  publications {
    maven(MavenPublication) {
      artifactId 'your-artifact'
      from components.java
    }
  }
  repositories {
    maven {
      url 'http://your.repo.com/repo/'
      lazyCredentials {
        passwordProperty "mvnPassword"
        usernameProperty "mvnUsername"
      }
    }
  }
}
```
You can also use `password` and `username` instead of
`passwordProperty` and `usernameProperty`. You can provide
String, GString or Closure as password and username. GString
and Closure will be evaluated only when necessary. It will
evaluate them only once though.

```
publishing {
  publications {
    maven(MavenPublication) {
      artifactId 'your-artifact'
      from components.java
    }
  }
  repositories {
    maven {
      url 'http://your.repo.com/repo/'
      lazyCredentials {
        password "${->lazy}"
        username {
          getUsername()
        }
      }
    }
  }
}
```

License
-------

See the LICENSE file for details.
