![kinta](docs/images/kinta.png)

See the [project website](https://dailymotion.github.io/kinta) for documentation and APIs.

Kinta is the swiss army knife for Mobile Apps automation. It helps you:

* build your app
* run your unit and UI tests
* upload your app to the google play
* upload screenshots, listing and changelogs to the google play
* handle beta distribution and testers
* manage translations, opening pull requests automatically 
* git housekeeping
* and much more!

Kinta is 100% written in Kotlin and can run in your CI or on your laptop.

# Work in progress

⚠️ Kinta is currently under heavy development. While it's working and used in production, the API might change at any time without warning.

# Installation

Install kinta:

```
curl -s 'https://dailymotion.github.io/kinta/install.sh' |sh

source ~/.kinta/setup-environment.sh
```

Then inside your app repo:

```
kinta init
```

This will create a `kintaSrc` directory where you can tweak your workflows.

# Integrations

Current integrations include:

* Appcenter
* Bitrise
* Google Cloud Storage
* Google Play
* Jira
* Transifex
* Slack

# Plug on Snapshots

Install kinta from master:

```
curl -s 'https://dailymotion.github.io/kinta/install_from_master.sh' |sh

```

If you have a `kintaSrc` project :

```
repositories {
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    api("com.dailymotion.kinta:kinta-lib:0.1.XX-SNAPSHOT")
    implementation("com.dailymotion.kinta:kinta-workflows:0.1.X-SNAPSHOT")    
}

```


# Contributing

Contributions and feedbacks are welcome. Please make sure to open an issue before starting working on something.
  
