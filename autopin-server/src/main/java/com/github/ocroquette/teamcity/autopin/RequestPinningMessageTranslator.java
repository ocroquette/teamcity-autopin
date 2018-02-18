package com.github.ocroquette.teamcity.autopin;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTranslator;
import jetbrains.buildServer.serverSide.BuildHistory;
import jetbrains.buildServer.serverSide.SRunningBuild;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class RequestPinningMessageTranslator implements ServiceMessageTranslator {
    private final Logger LOG = Logger.getLogger(Loggers.SERVER_CATEGORY);

    private final BuildHistory buildHistory;

    private static final String SM_NAME = "requestPinning";
    private final String ATTRIBUTE_INCLUDE_DEPENDENCIES = "includeDependencies";

    public RequestPinningMessageTranslator(@NotNull BuildHistory buildHistory) {
        this.buildHistory = buildHistory;
    }

    public static final String TAG_REQUEST_PINNING = "_request_pinning";
    public static final String TAG_REQUEST_PINNING_INCLUDE_DEPENDENCIES = "_request_pinning_include_dependencies";


    @NotNull
    public String getServiceMessageName() {
        return SM_NAME;
    }

    @NotNull
    public List<BuildMessage1> translate(SRunningBuild build, BuildMessage1 originalMessage, ServiceMessage serviceMessage) {
        boolean includeDependencies = getIncludeDependencies(serviceMessage);

        if (includeDependencies) {
            BuildTagHelper.addTag(build, TAG_REQUEST_PINNING_INCLUDE_DEPENDENCIES);
        }
        else {
            BuildTagHelper.addTag(build, TAG_REQUEST_PINNING);
        }

        List<BuildMessage1> buildMessages = new ArrayList<BuildMessage1>();
        buildMessages.add(originalMessage);
        return buildMessages;
    }

    private boolean getIncludeDependencies(ServiceMessage serviceMessage) {
        return StringUtils.isTrue(serviceMessage.getAttributes().get(ATTRIBUTE_INCLUDE_DEPENDENCIES));
    }
}