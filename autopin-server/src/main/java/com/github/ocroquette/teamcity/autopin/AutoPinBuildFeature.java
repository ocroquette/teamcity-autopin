package com.github.ocroquette.teamcity.autopin;

import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AutoPinBuildFeature extends BuildFeature {

    public static final String TYPE = AutoPinBuildFeature.class.getName();

    public static final String PARAM_STATUS = "status_radio";
    public static final String PARAM_STATUS_SUCCESSFUL = "Successful";
    public static final String PARAM_STATUS_FAILED = "Failed";
    public static final String PARAM_STATUS_ANY = "Any";

    public static final String PARAM_BRANCH_PATTERN = "branch_pattern";
    public static final String PARAM_COMMENT = "comment";
    public static final String PARAM_TAG = "tag";
    public static final String PARAM_PIN_DEPENDENCIES = "pin_dependencies";


    private final String myEditUrl;

    public AutoPinBuildFeature(@NotNull final PluginDescriptor descriptor) {
        myEditUrl = descriptor.getPluginResourcesPath("autopinBuildFeatureSettings.jsp");
    }


    @NotNull
    @Override
    public String getType() {
        return TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Pin the build";
    }

    @Nullable
    @Override
    public String getEditParametersUrl() {
        return myEditUrl;
    }

    @Override
    public boolean isMultipleFeaturesPerBuildTypeAllowed() {
        return true;
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> params) {
        StringBuilder sb = new StringBuilder();

        sb.append("Pin");

        if ( getParameterWithDefaults(params, PARAM_STATUS).equals(PARAM_STATUS_SUCCESSFUL))
            sb.append(" successfull");
        else if ( getParameterWithDefaults(params, PARAM_STATUS).equals(PARAM_STATUS_FAILED))
            sb.append(" failed");

        sb.append(" build");

        if (getParameterWithDefaults(params, PARAM_PIN_DEPENDENCIES).equals("true"))
            sb.append(" and all its dependencies");

        if (!getParameterWithDefaults(params, PARAM_BRANCH_PATTERN).isEmpty())
            sb.append(" if branch matches \"" + getParameterWithDefaults(params, PARAM_BRANCH_PATTERN) + "\"");

        if (!getParameterWithDefaults(params, PARAM_COMMENT).isEmpty())
            sb.append(" with comment \"" + getParameterWithDefaults(params, PARAM_COMMENT) + "\"");

        if (!getParameterWithDefaults(params, PARAM_TAG).isEmpty())
            sb.append(" and add tag \"" + getParameterWithDefaults(params, PARAM_TAG) + "\"");

        return sb.toString();
    }

    public String getParameterWithDefaults(Map<String, String> parameters, String name) {
        if (parameters.containsKey(name)) {
            return parameters.get(name);
        }

        Map<String, String> defaultParameters = getDefaultParameters();
        if (defaultParameters.containsKey(name)) {
            return defaultParameters.get(name);
        }

        return "UNDEFINED";
    }

    @Nullable
    @Override
    public PropertiesProcessor getParametersProcessor() {
        return new PropertiesProcessor() {
            public Collection<InvalidProperty> process(Map<String, String> params) {
                List<InvalidProperty> errors = new ArrayList<InvalidProperty>();
                return errors;
            }
        };
    }

    @Override
    public Map<String, String> getDefaultParameters() {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put(PARAM_STATUS, "Successful");
        map.put(PARAM_BRANCH_PATTERN, "");
        map.put(PARAM_COMMENT, "Pinned automatically");
        map.put(PARAM_TAG, "");
        map.put(PARAM_PIN_DEPENDENCIES, "true");
        return map;
    }
}