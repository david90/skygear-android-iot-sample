package io.skygear.skygear_starter_project;

import io.skygear.skygear.SkygearApplication;

public class MyApplication extends SkygearApplication {
    @Override
    public String getSkygearEndpoint() {
        return "https://iotsample.skygeario.com/";
    }

    @Override
    public String getApiKey() {
        return "fe8bc005f8cb4f8b92b187dee6e96f6f";
    }
}
