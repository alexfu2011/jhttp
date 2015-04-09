package eu.rekawek.jhttp.processor;

import java.io.File;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

public class ResourceNotFound implements RequestProcessor {

    public boolean process(HttpRequest request, HttpResponse response) {
        final File file = request.resolveFile();
        if (file.exists()) {
            return false;
        }
        response.setStatus(404, "File not found");
        response.getPrintWriter().println(String.format("File %s can't be found.", request.getUri()));
        return true;
    }
}