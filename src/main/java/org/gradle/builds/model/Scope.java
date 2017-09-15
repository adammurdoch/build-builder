package org.gradle.builds.model;

import java.io.PrintWriter;
import java.util.*;

public class Scope {
    private final Map<String, ScriptBlock> blocks = new LinkedHashMap<>();
    private final List<Expression> statements = new ArrayList<>();

    public Set<ScriptBlock> getBlocks() {
        return new LinkedHashSet<>(blocks.values());
    }

    public ScriptBlock block(String name) {
        return blocks.computeIfAbsent(name, k -> new ScriptBlock(name));
    }

    public void property(String name, CharSequence value) {
        addProperty(name, value);
    }

    public void property(String name, Number value) {
        addProperty(name, value);
    }

    public void property(String name, Code value) {
        addProperty(name, value);
    }

    private void addProperty(String name, Object value) {
        statements.add(new Expression() {
            @Override
            public void appendTo(PrintWriter printWriter) {
                if (value instanceof Number) {
                    printWriter.print(name + " = " + value);
                } else if (value instanceof Scope.Code) {
                    printWriter.print(name + " = ");
                    ((Code) value).appendTo(printWriter);
                } else {
                    printWriter.print(name + " = '" + value + "'");
                }
            }
        });
    }

    public List<Expression> getStatements() {
        return statements;
    }

    public void statement(String statement) {
        statements.add(new Code(statement));
    }

    public static abstract class Expression {
        public abstract void appendTo(PrintWriter printWriter);
    }

    public static class Code extends Expression {
        private final String code;

        public Code(String code) {
            this.code = code;
        }

        @Override
        public void appendTo(PrintWriter printWriter) {
            printWriter.append(code);
        }
    }
}
