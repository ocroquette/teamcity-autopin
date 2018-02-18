package com.github.ocroquette.teamcity.autopin;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTranslator;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.User;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AutopinServiceMessageTranslator implements ServiceMessageTranslator {
    private final Logger LOG = Logger.getLogger(Loggers.SERVER_CATEGORY);

    public AutopinServiceMessageTranslator() {
        LOG.info("AutopinServiceMessageTranslator constructor");
        Loggers.SERVER.info("AutopinServiceMessageTranslator constructor");
    }


    @NotNull
    public String getServiceMessageName() {
        return "addBuildTag";
    }

    @NotNull
    public List<BuildMessage1> translate(SRunningBuild build, BuildMessage1 originalMessage, ServiceMessage serviceMessage) {
        SProject project = build.getBuildType().getProject();
        long myId = build.getBuildId();
        User myUser = build.getTriggeredBy().getUser();
        LOG.info("AutopinServiceMessageTranslator: " + myId + ": ServiceMessageTranslator received message: " + serviceMessage);
        for (Map.Entry<String, String> entry: serviceMessage.getAttributes().entrySet() ) {
            LOG.info("AutopinServiceMessageTranslator: " + myId + ": ServiceMessageTranslator received message: " + entry.getKey() + "=" + entry.getValue());
        }

        BuildTagHelper.addTag(build, serviceMessage.getArgument());

        List<BuildMessage1> buildMessages = new ArrayList<BuildMessage1>();
        buildMessages.add(originalMessage);
        return buildMessages;
    }
}