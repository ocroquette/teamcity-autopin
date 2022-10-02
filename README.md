# Autopin plugin for Teamcity

This plugin allows to pin and/or tag Teamcity builds automatically, e.g. without human interaction. Here are some use cases:

* pin automatically a build run on a release branch
* tag the build with the version string generated in a build step
* when a build is parameterized, add a tag automatically accordingly, to see and filter easily the builds run with a given parameter value

## Installation

Currently, the plugin is available only as source code. To build it, clone the repository and run:

```
mvn package
```

Then copy the ZIP file ```target/autopin.zip``` in the plugins directory of your Teamcity instance. Finally, restart the server to load the plugin.

## Usage

### Pinning using build features

The easiest way to pin builds automatically is to add the build feature "Pin the build" provided by the plugin. It has the following parameters:

* Filters:
  * On status (successful, failed or any)
  * On branch name (see FAQ below)
* Other options:
  * Pin also build dependencies
  * Set comment
  * Optional tag

All filters must match for the pinning to occur, but you can add the build feature multiple times with different settings if required.

### Pinning using system messages

It is also possible to pin the build based on system messages, also known as [build script interaction](https://confluence.jetbrains.com/display/TCD10/Build+Script+Interaction+with+TeamCity).

```
##teamcity[requestPinning]
```

You can also request to pin the dependencies:

```
##teamcity[requestPinning includeDependencies='true']
```

Since Teamcity doesn't allow to pin a running build, a special build tag is set for the build, which is processed and removed once the build is finished.

### Tagging using build features

The plugin makes it also easy to add build tags automatically. Just  add the build feature "Tag the build" provided by the plugin. It has the following parameters:

* Filters:
  * On branch name (see FAQ below)
* Other options:
  * Tag also build dependencies

All filters must match for the tagging to occur, but you can add the build feature multiple times with different settings if required.

### Tagging using system messages

To add the build tag ```some_tag``` to the running build, use:

```
##teamcity[addBuildTag 'some_tag']
```

To add the build tag ```some_tag``` to the running build and all its dependencies, use:

```
##teamcity[addBuildTag tag='some_tag' includeDependencies='true']"
```


## FAQ
### My branch filter doesn't work. Why?

The branch filter is a regular expression that must match the complete branch name as displayed in the web interface. So if you want to match:

```
refs/heads/master
```

you will need the following regular expression:

```
refs/heads/master
```

It is not necessary to escape the slashes.

If you need to match release branches only, use:

```
refs/heads/release/.*
```

On a side note, you can use parenthesis in the branch definition of the VCS roots to get rid of the annoying prefix ```refs/heads``` when using Git.

