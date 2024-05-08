package fun.with;

import fun.with.annotations.Unstable;

import java.util.logging.Logger;

@Unstable
public class Printing {
    /**
     * Print the stack trace of an exception such that it stops printing shortly after we found the class behind instanceThatFailed moving up the stacktrace.
     * If instanceThatFailed refers to an object that you suspect, may cause an Exception, the exception will be printed from where it was caused until instanceThatFailed shows up in the stack trace.
     * Then a small number of stack trace elements is printed further for convenience.
     * @param instanceThatFailed object that may cause an exception or a Class in case of being called from a static method.
     * @param e the bad exception.
     */
    public static void printExceptionStub(Object instanceThatFailed, Exception e) {
        try {
            boolean instanceAvailable = instanceThatFailed != null;
            // if null take Printing, if instance is a Class itself (this method might be called from a static method), it is simply that class, if it is anything else, take the class of that.
            Class<?> logClass = instanceAvailable ? (instanceThatFailed instanceof Class ? (Class<?>) instanceThatFailed : instanceThatFailed.getClass()) : Printing.class;
            String logClassName = logClass.getName();
            Logger logger = Logger.getLogger(logClass.getName());
            StringBuilder b = new StringBuilder("'").append(logClass.getSimpleName()).append("' got a '").append(e.getClass().getSimpleName()).append("' here:\n");
            StackTraceElement[] stackTrace = e.getStackTrace();
            int maxLines = e.getStackTrace().length;
            int count = 0;
            while (count < maxLines) {
                StackTraceElement stackTraceElement = stackTrace[count];
                b.append("       [") //
                        .append(count).append("] Class '") //
                        .append(stackTraceElement.getClassName()) //
                        .append("', method '") //
                        .append(stackTraceElement.getMethodName()) //
                        .append("', line ") //
                        .append(stackTraceElement.getLineNumber()) //
                        .append("\n");
                // the calling class has been found. time to stop soon.
                if (stackTraceElement.getClassName().equals(logClassName)) {
                    // print some more lines if available
                    maxLines = Math.min(count + 5, maxLines);
                }
                count++;
            }
            logger.warning(b.toString());
        } catch (Exception exception) {
            // just in case this implementation also fails
            System.err.println("Could not print exception because of this:");
            exception.printStackTrace();
            System.err.println("Initial exception:");
            e.printStackTrace();
        }
    }
}
