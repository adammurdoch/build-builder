package org.gradle.builds;

import java.util.ArrayList;
import java.util.List;

public class ProblemCollector {
    private final List<String> messages = new ArrayList<>();

    public void problem(String message) {
        messages.add(message);
    }

    public void assertNoIssues() {
        if (messages.isEmpty()) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (String message : messages) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(message);
        }
        throw new IllegalStateException(builder.toString());
    }
}
