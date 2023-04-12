package tools;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.JobFlowBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutor;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.job.flow.State;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

public class DumpPlantUmlTool {
    private PrintWriter out = new PrintWriter(System.out);

    @Test
    public void dump() {
        dumpClass(ItemReader.class);
        dumpClass(ItemProcessor.class);
        dumpClass(ItemWriter.class);
    }

    @Test
    public void dumpFlow() {
        dumpClass(Flow.class);
        dumpClass(State.class);
        dumpClass(FlowExecutor.class);
    }

    @Test
    public void dumpValidator() {
        dumpClass(Validator.class);
        dumpClass(ValidationException.class);
        dumpClass(SpringValidator.class);
        dumpClass(ValidatingItemProcessor.class);
        dumpClass(BeanValidatingItemProcessor.class);
        dumpClass(org.springframework.validation.Validator.class);
    }

    @Test
    public void dumpFlowBuilder() {
        dumpClass(JobBuilder.class);
        dumpClass(SimpleJobBuilder.class);
        dumpClass(FlowBuilder.class);
        dumpClass(FlowBuilder.SplitBuilder.class);
        dumpClass(FlowBuilder.TransitionBuilder.class);
        dumpClass(FlowBuilder.UnterminatedFlowBuilder.class);
        dumpClass(JobFlowBuilder.class);
        dumpClass(FlowJobBuilder.class);
        dumpClass(JobExecutionDecider.class);
    }

    @Test
    public void dumpJobExecutionListener() {
        dumpClass(JobExecutionListener.class);
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
