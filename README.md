# Autopin plugin for Teamcity

This plugin allows to pin and tag Teamcity builds automatically, e.g. without human interaction.

## Warning

This plugin is still in early development, it might undergo changes that are not backwards compatible.

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
  * On branch name
* Other options:
  * Pin also build dependencies
  * Set comment
  * Optional tag


### Pinning using system messages

It is also possible to pin the build based on system messages, also known as [build script interaction](https://confluence.jetbrains.com/display/TCD10/Build+Script+Interaction+with+TeamCity).

Since Teamcity doesn't allow to pin a running build directly, a special build tag is set for the build, which is processed and removed once the build finished.

```
##teamcity[addBuildTag 'autopin']
```

You can also request to pin the dependencies:

```
##teamcity[addBuildTag 'autopin_include_dependencies']
```

### Tagging using build features

The plugin makes it also easy to add build tags automatically. Just  add the build feature "Tag the build" provided by the plugin. It has the following parameters:

* Filters:
  * On branch name
* Other options:
  * Tag also build dependencies


### Tagging using system messages

To add the build tag ```some_tag``` to the running build, use:

```
##teamcity[addBuildTag 'some_tag']
```

To add the build tag ```some_tag``` to the running build and all its dependencies, use:

```
##teamcity[addBuildTag tag='some_tag' includeDependencies='true']"
```

## TODO

* Introduce specific service messages for pinning request

