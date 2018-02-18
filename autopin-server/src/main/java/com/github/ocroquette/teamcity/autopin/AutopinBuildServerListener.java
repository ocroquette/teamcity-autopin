package com.github.ocroquette.teamcity.autopin;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.impl.LogUtil;
import jetbrains.buildServer.users.User;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.github.ocroquette.teamcity.autopin.RequestPinningMessageTranslator.TAG_REQUEST_PINNING;
import static com.github.ocroquette.teamcity.autopin.RequestPinningMessageTranslator.TAG_REQUEST_PINNING_INCLUDE_DEPENDENCIES;


public class AutopinBuildServerListener extends BuildServerAdapter {

    private final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Loggers.SERVER_CATEGORY);
    private final BuildHistory buildHistory;


    public AutopinBuildServerListener(@NotNull EventDispatcher<BuildServerListener> events,
                                      @NotNull BuildHistory buildHistory) {
        LOG.info("AutopinBuildServerListener constructor");
        events.addListener(this);
        this.buildHistory = buildHistory;
    }

    @Override
    public void buildStarted(@NotNull SRunningBuild runningBuild) {
        User triggeringUser = runningBuild.getTriggeredBy().getUser();
        for (SBuildFeatureDescriptor bfd : runningBuild.getBuildFeaturesOfType(AutoTagBuildFeature.TYPE)) {
            String tag = bfd.getParameters().get(AutoTagBuildFeature.PARAM_TAG);

            if (areTaggingConditionsMet(bfd.getParameters(), runningBuild)) {
                BuildTagHelper.addTag(runningBuild, tag);

                if (StringUtils.isTrue(bfd.getParameters().get(AutoTagBuildFeature.PARAM_TAG_DEPENDENCIES))) {
                    for (BuildPromotion bp : runningBuild.getBuildPromotion().getAllDependencies()) {
                        BuildTagHelper.addTag(buildHistory.findEntry(bp.getAssociatedBuild().getBuildId()), tag);
                    }
                }
            }
        }
    }

    @Override
    public void buildFinished(@NotNull SRunningBuild build) {
        Loggers.SERVER.info("buildFinished: " + LogUtil.describe(build));
        Loggers.SERVER.info("buildFinished: branch=" + build.getBranch());
        if ( build.getBranch() != null ) {
            Loggers.SERVER.info("buildFinished: branch name=" + build.getBranch().getName());
            Loggers.SERVER.info("buildFinished: branch displayName=" + build.getBranch().getDisplayName());
        }

        final SFinishedBuild finishedBuild = buildHistory.findEntry(build.getBuildId());

        User triggeringUser = build.getTriggeredBy().getUser();

        if (finishedBuild.getTags().contains(TAG_REQUEST_PINNING) || finishedBuild.getTags().contains(TAG_REQUEST_PINNING_INCLUDE_DEPENDENCIES)) {

            String comment = "Pinned automatically based on service message in build #" + finishedBuild.getBuildId();

            finishedBuild.setPinned(true, triggeringUser, comment);

            if (finishedBuild.getTags().contains(TAG_REQUEST_PINNING_INCLUDE_DEPENDENCIES)) {
                List<? extends BuildPromotion> allDependencies = finishedBuild.getBuildPromotion().getAllDependencies();

                for (BuildPromotion bp : allDependencies) {
                    LOG.info("Pinning dependency: " + bp.getAssociatedBuild());
                    buildHistory.findEntry(bp.getAssociatedBuild().getBuildId()).setPinned(true, triggeringUser, comment);
                }
            }

            BuildTagHelper.removeTag(finishedBuild, TAG_REQUEST_PINNING);
            BuildTagHelper.removeTag(finishedBuild, TAG_REQUEST_PINNING_INCLUDE_DEPENDENCIES);
        }

        for (SBuildFeatureDescriptor bfd : finishedBuild.getBuildFeaturesOfType(AutoPinBuildFeature.TYPE)) {
            if (arePinningConditionsMet(bfd.getParameters(), finishedBuild)) {
                String comment = bfd.getParameters().get(AutoPinBuildFeature.PARAM_COMMENT);
                finishedBuild.setPinned(true, triggeringUser, comment);

                if (StringUtils.isTrue(bfd.getParameters().get(AutoPinBuildFeature.PARAM_PIN_DEPENDENCIES))) {
                    for (BuildPromotion bp : finishedBuild.getBuildPromotion().getAllDependencies()) {
                        buildHistory.findEntry(bp.getAssociatedBuild().getBuildId()).setPinned(true, triggeringUser, comment);
                    }
                }
            }
        }
    }

    private boolean arePinningConditionsMet(Map<String, String> parameters, SBuild build) {
        boolean matching = true;

        String requestedStatus = parameters.get(AutoPinBuildFeature.PARAM_STATUS);
        if (requestedStatus != null) {
            if (requestedStatus.equals(AutoPinBuildFeature.PARAM_STATUS_SUCCESSFUL)) {
                matching = matching && build.getBuildStatus().equals(Status.NORMAL);
            } else if (requestedStatus.equals(AutoPinBuildFeature.PARAM_STATUS_FAILED)) {
                matching = matching && !build.getBuildStatus().equals(Status.NORMAL);
            }
        }

        String requestedBranchPattern = parameters.get(AutoPinBuildFeature.PARAM_BRANCH_PATTERN);
        if (requestedBranchPattern != null && !requestedBranchPattern.isEmpty() && build.getBranch() != null) {
            matching = matching && build.getBranch().getDisplayName().matches(requestedBranchPattern);
        }

        return matching;
    }

    private boolean areTaggingConditionsMet(Map<String, String> parameters, SBuild build) {
        boolean matching = true;

        String requestedBranchPattern = parameters.get(AutoPinBuildFeature.PARAM_BRANCH_PATTERN);
        if (requestedBranchPattern != null && !requestedBranchPattern.isEmpty()  && build.getBranch() != null) {
            matching = matching && build.getBranch().getDisplayName().matches(requestedBranchPattern);
        }

        return matching;
    }
}
