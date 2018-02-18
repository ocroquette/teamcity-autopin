package com.github.ocroquette.teamcity.autopin;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTranslator;
import jetbrains.buildServer.serverSide.BuildHistory;
import jetbrains.buildServer.serverSide.BuildPromotion;
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

    private final BuildHistory buildHistory;

    private final String ATTRIBUTE_TAG = "tag";
    private final String ATTRIBUTE_INCLUDE_DEPENDENCIES = "includeDependencies";

    public AutopinServiceMessageTranslator(@NotNull BuildHistory buildHistory) {
        this.buildHistory = buildHistory;
    }


    @NotNull
    public String getServiceMessageName() {
        return "addBuildTag";
    }

    @NotNull
    public List<BuildMessage1> translate(SRunningBuild build, BuildMessage1 originalMessage, ServiceMessage serviceMessage) {
        String tag = getTag(serviceMessage);
        boolean includeDependencies = getIncludeDependencies(serviceMessage);

        if (tag.isEmpty()) {
            LOG.warn("No tag provided for " + getServiceMessageName() + " in build " + build.getBuildId());
        } else {
            BuildTagHelper.addTag(build, tag);
            if (includeDependencies) {
                for (BuildPromotion bp : build.getBuildPromotion().getAllDependencies()) {
                    BuildTagHelper.addTag(buildHistory.findEntry(bp.getAssociatedBuild().getBuildId()), tag);
                }
            }
        }

        List<BuildMessage1> buildMessages = new ArrayList<BuildMessage1>();
        buildMessages.add(originalMessage);
        return buildMessages;
    }

    private String getTag(ServiceMessage serviceMessage) {
        if (serviceMessage.getArgument() != null && !serviceMessage.getArgument().isEmpty()) {
            return serviceMessage.getArgument();
        } else if (serviceMessage.getAttributes().get(ATTRIBUTE_TAG) != null && !serviceMessage.getAttributes().get(ATTRIBUTE_TAG).isEmpty()) {
            return serviceMessage.getAttributes().get(ATTRIBUTE_TAG);
        } else {
            return "";
        }
    }

    private boolean getIncludeDependencies(ServiceMessage serviceMessage) {
        return StringUtils.isTrue(serviceMessage.getAttributes().get(ATTRIBUTE_INCLUDE_DEPENDENCIES));
    }
}