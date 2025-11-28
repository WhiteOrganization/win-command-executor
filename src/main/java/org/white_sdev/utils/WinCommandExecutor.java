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

        if (textToSpeechMessage == null || textToSpeechMessage.isBlank()) {
            log.warn("Empty TTS message");
            return false;
        }

        // Prefer PowerShell System.Speech (more robust on many Windows setups)
        try {
            // Escape single quotes for PowerShell single-quoted string by doubling them
            String safeForPs = textToSpeechMessage.replace("'", "''");
            String psCommand = String.format("powershell.exe -NoProfile -Command \"Add-Type -AssemblyName System.Speech; (New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('%s')\"", safeForPs);
            boolean psResult = executeCommandInConsole(psCommand);
            log.trace("PowerShell TTS result: {}", psResult);
            if (psResult) return true;
            log.warn("PowerShell TTS returned false, attempting mshta fallback");
        } catch (Exception e) {
            log.warn("PowerShell TTS attempt threw an exception", e);
        }

        // Fallback: create a temporary .vbs file and run it with cscript (more robust than mshta)
        try {
            String safeForVbs = textToSpeechMessage.replace("\"", "\"\"");
            String vbsContent = "CreateObject(\"SAPI.SpVoice\").Speak \"" + safeForVbs + "\"";
            java.nio.file.Path temp = java.nio.file.Files.createTempFile("tts", ".vbs");
            java.nio.file.Files.writeString(temp, vbsContent);
            String cscriptCmd = String.format("cscript //NoLogo %s", temp.toAbsolutePath().toString());
            boolean vbsResult = executeCommandInConsole(cscriptCmd);
            try {
                java.nio.file.Files.deleteIfExists(temp);
            } catch (Exception ignore) {}
            log.trace("vbs TTS result: {}", vbsResult);
            return vbsResult;
        } catch (Exception e) {
            log.error("VBS TTS attempt failed", e);
            return false;
        }
    }

    @SuppressWarnings("all")
    public static boolean sendErrorSound() {
        return sendTextToSpeechMessage("Error");
    }
}