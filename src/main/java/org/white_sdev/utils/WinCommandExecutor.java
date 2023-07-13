package org.white_sdev.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Slf4j
public class WinCommandExecutor {

    public static class WinCommandExecutorCommand {
        String command;
        String successTextToSpeechMessage;
        String errorMessage;

        public WinCommandExecutorCommand(String command, String successTextToSpeechMessage, String errorMessage) {
            this.command = command;
            this.successTextToSpeechMessage = successTextToSpeechMessage;
            this.errorMessage = errorMessage;
        }

        @Override
        public String toString() {
            return "\n{" +
                    "command='" + command + '\'' + "\t" +
                    ", successTextToSpeechMessage='" + successTextToSpeechMessage + '\'' + "\t" +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        String logID = "::main([args]): ";
        log.trace("{}Start", logID);

        SpringApplication.run(WinCommandExecutor.class, args);

        log.info("Initiating process");

        if (args == null || args.length == 0) {
            sendTextToSpeechMessage("No commands provided");
            System.exit(1);
        }

        List<WinCommandExecutorCommand> commands = new ArrayList<>(args.length / 3);

        for (int i = 0; i < args.length; ) commands.add(new WinCommandExecutorCommand(args[i++], args[i++], args[i++]));

        log.info("{} commands:{}", logID, commands);
        sendTextToSpeechMessage("Starting");

        for (var command : commands)
            if (executeCommandInConsole(command.command))
                sendTextToSpeechMessage(command.successTextToSpeechMessage);
            else
                processErrorAndExit(command.errorMessage);
        sendTextToSpeechMessage("Finished");
        log.info("Finish");
        System.exit(0);
    }

    static void processErrorAndExit(String errorMessage) {
        String logID = "::error([errorMessage]): ";
        log.trace("{}Start - errorMessage:{}", logID, errorMessage);
        sendErrorSound();
        log.error("{}", errorMessage);
        System.exit(1);
    }

    @SneakyThrows
    private static boolean executeCommandInConsole(String command) {
        String logID = "::executeCommandInConsole([command]): ";
        log.trace("{}Start - command:{}", logID, command);
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        processBuilder.inheritIO(); // Redirects the output to the console
        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        log.debug("{}Finish - Command `{}` exited with code:{}", logID, command, exitCode);
        return exitCode == 0;
    }

    @SuppressWarnings("all")
    static boolean sendTextToSpeechMessage(String textToSpeechMessage) {
        String logID = "::sendTextToSpeechMessage([textToSpeechMessage]): ";
        logID = "";
        log.trace("{}Start - textToSpeechMessage:{}", logID, textToSpeechMessage);
        String textToSpeechCommand = String.format("mshta vbscript:Execute(" +
                "\"CreateObject(\"\"SAPI.SpVoice\"\").Speak(\"\"%s\"\")(window.close)\")", textToSpeechMessage);
        var result = executeCommandInConsole(textToSpeechCommand);
        log.trace("{}Finish - result:{}", logID, result);
        return result;
    }

    @SuppressWarnings("all")
    public static boolean sendErrorSound() {
        return sendTextToSpeechMessage("Error");
    }
}