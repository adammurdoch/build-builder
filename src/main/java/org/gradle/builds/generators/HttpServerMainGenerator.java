package org.gradle.builds.generators;

import org.gradle.builds.model.HttpServerImplementation;
import org.gradle.builds.model.Project;

import java.io.PrintWriter;

public class HttpServerMainGenerator extends ComponentSpecificProjectFileGenerator<HttpServerImplementation> {
    public HttpServerMainGenerator() {
        super(HttpServerImplementation.class, "src/main/java/org/gradle/example/http/RepoMain.java");
    }

    @Override
    protected void generate(Project project, HttpServerImplementation component, PrintWriter printWriter) {
        printWriter.println("package org.gradle.example.http;");
        printWriter.println();
        printWriter.println("import com.sun.net.httpserver.HttpServer;");
        printWriter.println("import com.sun.net.httpserver.HttpHandler;");
        printWriter.println("import com.sun.net.httpserver.HttpExchange;");
        printWriter.println("import java.net.InetSocketAddress;");
        printWriter.println("import java.io.IOException;");
        printWriter.println("import java.io.File;");
        printWriter.println("import java.nio.file.Files;");
        printWriter.println("import java.net.URI;");
        printWriter.println("import java.util.concurrent.atomic.AtomicLong;");
        printWriter.println();
        printWriter.println("public class RepoMain {");
        printWriter.println("    public static void main(String[] args) throws Exception {");
        printWriter.println("        File rootDir = new File(new URI(\"" + component.getRootDir().toUri() + "\"));");
        printWriter.println("        System.out.println(\"Root dir: \" + rootDir);");
        printWriter.println("        System.out.println(\"URL: http://localhost:" + component.getPort() + "\");");
        printWriter.println("        HttpServer server = HttpServer.create(new InetSocketAddress(" + component.getPort() + "), 20);");
        printWriter.println("        AtomicLong counter = new AtomicLong();");
        printWriter.println("        server.createContext(\"/\", new HttpHandler() {");
        printWriter.println("            public void handle(HttpExchange exchange) throws IOException {");
        printWriter.println("                long n = counter.incrementAndGet();");
        printWriter.println("                String p = exchange.getRequestURI().getPath().substring(1);");
        printWriter.println("                System.out.println(String.format(\"[%d] request %s %s\", n, exchange.getRequestMethod(), p));");
        printWriter.println("                boolean getRequest = exchange.getRequestMethod().equals(\"GET\");");
        printWriter.println("                File f = new File(rootDir, p);");
        printWriter.println("                System.out.println(String.format(\"[%d] maps to file: %s\", n, f));");
        printWriter.println("                if (!f.isFile()) {");
        printWriter.println("                    System.out.println(String.format(\"[%d] not found.\", n));");
        printWriter.println("                    exchange.sendResponseHeaders(404, -1);");
        printWriter.println("                } else if (!getRequest) {");
        printWriter.println("                    System.out.println(String.format(\"[%d] found.\", n));");
        printWriter.println("                    exchange.sendResponseHeaders(200, -1);");
        printWriter.println("                } else {");
        printWriter.println("                    System.out.println(String.format(\"[%d] sending.\", n));");
        printWriter.println("                    exchange.sendResponseHeaders(200, f.length());");
        printWriter.println("                    Files.copy(f.toPath(), exchange.getResponseBody());");
        printWriter.println("                }");
        printWriter.println("                exchange.close();");
        printWriter.println("            }");
        printWriter.println("        });");
        printWriter.println("        server.start();");
        printWriter.println("    }");
        printWriter.println("}");
    }
}
