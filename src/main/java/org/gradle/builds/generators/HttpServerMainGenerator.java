package org.gradle.builds.generators;

import org.gradle.builds.model.HttpServer;
import org.gradle.builds.model.Project;

import java.io.PrintWriter;

public class HttpServerMainGenerator extends ComponentSpecificProjectFileGenerator<HttpServer> {
    public HttpServerMainGenerator() {
        super(HttpServer.class, "src/main/java/org/gradle/example/http/Main.java");
    }

    @Override
    protected void generate(Project project, HttpServer component, PrintWriter printWriter) {
        printWriter.println("package org.gradle.example.http;");
        printWriter.println();
        printWriter.println("import com.sun.net.httpserver.HttpServer;");
        printWriter.println("import com.sun.net.httpserver.HttpHandler;");
        printWriter.println("import com.sun.net.httpserver.HttpExchange;");
        printWriter.println("import java.net.InetSocketAddress;");
        printWriter.println("import java.io.IOException;");
        printWriter.println("import java.io.File;");
        printWriter.println("import java.net.URI;");
        printWriter.println();
        printWriter.println("public class Main {");
        printWriter.println("    public static void main(String[] args) throws Exception {");
        printWriter.println("        File rootDir = new File(new URI(\"" + component.getRootDir().toUri() + "\"));");
        printWriter.println("        System.out.println(\"Root dir: \" + rootDir);");
        printWriter.println("        System.out.println(\"URL: http://localhost:" + component.getPort() + "\");");
        printWriter.println("        HttpServer server = HttpServer.create(new InetSocketAddress(" + component.getPort() + "), 20);");
        printWriter.println("        server.createContext(\"/\", new HttpHandler() {");
        printWriter.println("            public void handle(HttpExchange exchange) throws IOException {");
        printWriter.println("                String p = exchange.getRequestURI().getPath().substring(1);");
        printWriter.println("                System.out.println(\"-> \" + p);");
        printWriter.println("                File f = new File(rootDir, p);");
        printWriter.println("                System.out.println(\"-> \" + f);");
        printWriter.println("                exchange.sendResponseHeaders(200, 0);");
        printWriter.println("                exchange.close();");
        printWriter.println("            }");
        printWriter.println("        });");
        printWriter.println("        server.start();");
        printWriter.println("    }");
        printWriter.println("}");
    }
}
