��#   M y - T o a s t 

> Step 1. Add the JitPack repository to your build file

```gradle
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
  ```

> Step 2. Add the dependency

```gradle
  dependencies {
	        implementation 'com.github.MArslan88:My-Toast:1.0.0'
	}
  ```
 
 
