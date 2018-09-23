package org.loguno.plugin;

import com.google.auto.service.AutoService;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;


import java.util.*;
import java.util.stream.Collectors;

@AutoService(Plugin.class)
public class LogunoPlugin implements Plugin {

    public static final String NAME = "Loguno";

    ThreadLocal<LinkedList<String>> local = ThreadLocal.withInitial(LinkedList::new);

    private static Set<String> TARGET_TYPES = new HashSet<>(Arrays.asList(
            // Use only primitive types for simplicity
            byte.class.getName(), short.class.getName(), char.class.getName(),
            int.class.getName(), long.class.getName(), float.class.getName(), double.class.getName()));

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void init(JavacTask task, String... args) {
        final Context context = ((BasicJavacTask) task).getContext();

        final Log logger = Log.instance(context);

        task.addTaskListener(new TaskListener() {

            @Override
            public void started(TaskEvent taskEvent) {
                local.get().add(">");
                printEvent(taskEvent, "started");
            }

            @Override
            public void finished(TaskEvent taskEvent) {
                printEvent(taskEvent, "finished");
                local.get().removeLast();
            }

            private void printEvent(TaskEvent event, String step) {

                StringBuilder sb = new StringBuilder();

                String offset = local.get().stream().collect(Collectors.joining());

                sb.append(offset)
                        .append(step)
                        .append("...")
                        .append("  e:" + event.getKind())
                        //.append("  CompilationUnit:" + event.getCompilationUnit())
                        .append("\t TypeElement:" + event.getTypeElement());

                System.out.println(sb.toString());
                //logger.printRawLines(Log.WriterKind.NOTICE, sb.toString());
            }

        });
    }
}
