package eu.rekawek.jhttp;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.rekawek.jhttp.api.RequestProcessor;
import eu.rekawek.jhttp.processor.DirectoryIndex;
import eu.rekawek.jhttp.processor.DirectoryListing;
import eu.rekawek.jhttp.processor.ResourceNotFound;
import eu.rekawek.jhttp.processor.StaticFile;

public class HttpServer {

    private static final int HTTP_PORT = 8888;

    private static final int THREADS_NO = 10;

    private final ExecutorService executor;

    private final List<RequestProcessor> processors;

    private ServerSocket serverSocket;

    public HttpServer(final File serverRoot) {
        this.executor = Executors.newFixedThreadPool(THREADS_NO);

        final FileResolver fileResolver = new FileResolver(serverRoot);
        processors = new ArrayList<>();
        processors.add(new DirectoryIndex(fileResolver));
        processors.add(new DirectoryListing(fileResolver));
        processors.add(new StaticFile(fileResolver));
        processors.add(new ResourceNotFound(fileResolver));
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(HTTP_PORT);
        do {
            final Socket clientSocket = serverSocket.accept();
            final ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket, processors);
            executor.submit(connectionHandler);
        } while (!serverSocket.isClosed());
    }

    public void stop() throws IOException {
        serverSocket.close();
    }
}
