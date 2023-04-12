package remote.tools;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilder;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilderFactory;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilder;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilder;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;

public class DumpPlantUmlTool {
    private PrintWriter out = new PrintWriter(System.out);

    @Test
    public void dumpRemoteChunking() {
        dumpClass(RemoteChunkingManagerStepBuilderFactory.class);
        dumpClass(RemoteChunkingManagerStepBuilder.class);
        dumpClass(RemoteChunkingWorkerBuilder.class);
    }

    @Test
    public void dumpRemotePartitioning() {
        dumpClass(RemotePartitioningManagerStepBuilderFactory.class);
        dumpClass(RemotePartitioningManagerStepBuilder.class);
        dumpClass(RemotePartitioningWorkerStepBuilderFactory.class);
        dumpClass(RemotePartitioningWorkerStepBuilder.class);
    }

    @AfterEach
    void finish() {
        out.flush();
    }

    private void dumpClass(Class<?> clazz) {
        out.printf("%s %s%s {%n", type(clazz), clazz.getSimpleName(), generics(clazz));
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                dumpMethod(method);
            }
        }
        out.printf("}%n", clazz.getSimpleName());
    }

    private String type(Class<?> clazz) {
        if (clazz.isInterface()) {
            return "interface";
        } else if (clazz.isEnum()) {
            return "enum";
        } else {
            return "class";
        }
    }

    private Object generics(Class<?> clazz) {
        TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
        if (typeParameters == null || typeParameters.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        for (int i = 0; i < typeParameters.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            TypeVariable<?> typeVariable = typeParameters[i];
            sb.append(typeVariable.getTypeName());
        }
        sb.append(">");
        return sb.toString();
    }

    private void dumpMethod(Method method) {
        out.printf("    %s(", method.getName());
        boolean first = true;
        for (Parameter parameter : method.getParameters()) {
            if (first) {
                first = false;
            } else {
                out.print(", ");
            }
            out.printf("%s: %s", parameterName(parameter), renderType(parameter.getParameterizedType()));
        }
        out.print(")");
        if (method.getReturnType() != Void.TYPE) {
            out.printf(": %s", renderType(method.getGenericReturnType()));
        }
        out.println();
    }

    private String parameterName(Parameter parameter) {
        String name = parameter.getName();
        if ("arg0".equals(name)) {
            name = decapitalize(parameter.getType().getSimpleName());
        }
        return name;
    }

    private String decapitalize(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    private String renderType(Type type) {
        String typeName = type.getTypeName();
        typeName = typeName.substring(typeName.lastIndexOf('.') + 1);
        return typeName.replace('$', '.');
    }
}
