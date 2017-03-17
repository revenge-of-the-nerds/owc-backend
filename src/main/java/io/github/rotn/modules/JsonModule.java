package io.github.rotn.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import io.github.rotn.annot.Module;

@Module("json")
public final class JsonModule
extends AbstractModule{
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    protected void configure() {
        this.bind(Gson.class).toInstance(this.gson);
    }
}