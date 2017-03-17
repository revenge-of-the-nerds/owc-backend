package io.github.rotn.services;

import io.github.rotn.annot.RequestHandler;
import io.github.rotn.annot.Service;
import spark.Request;
import spark.Response;

@Service
public final class VersionService{
    @RequestHandler(path = "/cat/:name")
    private String getVersion(Request req, Response resp){
        return "Hello, " + req.params("name") + "!";
    }
}