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

Currently, the plugin uses system messages, also known as [build script interaction](https://confluence.jetbrains.com/display/TCD10/Build+Script+Interaction+with+TeamCity).

To add a build tag to the running build, use:


```
##teamcity[addBuildTag 'some_tag']
```

To add the build tag ```some_tag``` to the running build and all its dependencies, use:

```
echo "##teamcity[addBuildTag 'tc_autotag_with_dependencies_some_tag']"
```

The tag ```tc_autotag_with_dependencies_some_tag``` will be added immediatly, and processed by the plugin once the build finishes.

To pin a build automaticy once it finishes, add the following special tag:

```
echo "##teamcity[addBuildTag 'tc_autopin']"
```

And to pin also the depending builds:

```
echo "##teamcity[addBuildTag 'tc_autopin_with_dependencies']"
```

## TODO

* Introduce build features to autotag and autopin

