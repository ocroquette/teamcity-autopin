package com.github.ocroquette.teamcity.autopin;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.impl.LogUtil;
import jetbrains.buildServer.users.User;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class AutopinBuildServerListener extends BuildServerAdapter {
    public static final String TAG_PIN = "tc_autopin";
    public static final String TAG_PIN_WITH_DEPENDENCIES = "tc_autopin_with_dependencies";
    public static final String TAG_TAG_DEPENDENCIES_PREFIX = "tc_autotag_with_dependencies_";

    private final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Loggers.SERVER_CATEGORY);
    private final BuildHistory myBuildHistory;


    public AutopinBuildServerListener(@NotNull EventDispatcher<BuildServerListener> events,
                                      @NotNull BuildHistory buildHistory) {
        LOG.info("AutopinBuildServerListener constructor");
        events.addListener(this);
        myBuildHistory = buildHistory;
    }

    @Override
    public void buildFinished(@NotNull SRunningBuild build) {
        Loggers.SERVER.info("buildFinished: " + LogUtil.describe(build));

        final SFinishedBuild finishedBuild = myBuildHistory.findEntry(build.getBuildId());

        User triggeringUser = build.getTriggeredBy().getUser();

        if (finishedBuild.getTags().contains(TAG_PIN) || finishedBuild.getTags().contains(TAG_PIN_WITH_DEPENDENCIES)) {

            String comment = "Pinned automatically based on service message (" + TAG_PIN + ") in build #" + finishedBuild.getBuildId();

            finishedBuild.setPinned(true, triggeringUser, comment);

            if (finishedBuild.getTags().contains(TAG_PIN_WITH_DEPENDENCIES)) {
                List<? extends BuildPromotion> allDependencies = finishedBuild.getBuildPromotion().getAllDependencies();

                for (BuildPromotion bp : allDependencies) {
                    LOG.info("Pinning dependency: " + bp.getAssociatedBuild());
                    myBuildHistory.findEntry(bp.getAssociatedBuild().getBuildId()).setPinned(true, triggeringUser, comment);
                }
            }

            BuildTagHelper.removeTag(finishedBuild, TAG_PIN);
            BuildTagHelper.removeTag(finishedBuild, TAG_PIN_WITH_DEPENDENCIES);
        }
        for (String tag : finishedBuild.getTags()) {
            if (tag.startsWith(TAG_TAG_DEPENDENCIES_PREFIX)) {
                String tag2 = tag.substring(TAG_TAG_DEPENDENCIES_PREFIX.length());
                for (BuildPromotion bp : finishedBuild.getBuildPromotion().getAllDependencies()) {
                    LOG.info("Tagging dependency: " + bp.getAssociatedBuild() + " with " + tag2);
                    BuildTagHelper.addTag(myBuildHistory.findEntry(bp.getAssociatedBuild().getBuildId()), tag2);
                }
                BuildTagHelper.removeTag(finishedBuild, tag);
            }
        }

    }
}
