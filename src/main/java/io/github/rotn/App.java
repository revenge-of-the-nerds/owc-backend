package io.github.rotn;

import io.github.rotn.annot.ModulesPath;
import io.github.rotn.modules.Modules;
import io.github.rotn.services.Services;

@ModulesPath("io.github.rotn.modules")
public final class App{
    public static void main(String... args) throws Exception{
        Modules.install(App.class);
        Services.loadAll();
    }
}