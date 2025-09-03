package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.ninja.NinjaEmailInfo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.naming.*;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements EmailSender {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    private static final String UTF_ENCODING = "UTF-8";

    @Value("${app.email.from:mayvi9609@gmail.com}")
    private String fromEmail;

    @Value("${app.email.from-name:Equipo Nara Logic🥷}")
    private String fromName;

    @Value("${app.dashboard.url:http://localhost:8080/dashboard}")
    private String dashboardUrl;

    @Override
    public void sendNinjaWelcomeEmail(String toNinja, String ninjaName, String village) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, UTF_ENCODING);

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toNinja);
            helper.setSubject("¡Bienvenide al mundo ninja, " + ninjaName + "!");

            Context context = new Context();
            context.setVariable("ninjaName", ninjaName);
            context.setVariable("village", village);
            context.setVariable("dashboardUrl", dashboardUrl);

            String htmlContent = templateEngine.process("welcome-notification", context);

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Correo de bienvenida enviado con éxito a: {}", toNinja);

        } catch (MessagingException | UnsupportedEncodingException exception) {
            log.error("Error al enviar el correo de bienvenida a {}: {}", toNinja, exception.getMessage(), exception);
        }
    }

    @Override
    public void sendMissionAssignmentEmail(List<NinjaEmailInfo> ninjaTeam, String missionTitle, String missionDescription, String missionRank) {
        if (ninjaTeam == null || ninjaTeam.isEmpty()){
            log.warn("Intento de enviar email de misión a equipo vacío para la misión: {}", missionTitle);
            return;
        }

        boolean isTeamMission = ninjaTeam.size() > 1;
        String subject = isTeamMission ? "¡Nueva misión de equipo asignada: " + missionTitle + "!" : "¡Nueva misión asignada: " + missionTitle + "!";

        log.info("Enviando emails de misión de equipo para: {} a {} ninjas", missionTitle, ninjaTeam.size());

        for (NinjaEmailInfo ninja : ninjaTeam){
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, UTF_ENCODING);

                helper.setFrom(fromEmail, fromName);
                helper.setTo(ninja.email());
                helper.setSubject(subject);

                Context context = new Context();
                context.setVariable("ninjaName", ninja.name());
                context.setVariable("missionTitle", missionTitle);
                context.setVariable("missionDescription", missionDescription);
                context.setVariable("missionRank", missionRank);
                context.setVariable("dashboardUrl", dashboardUrl);
                context.setVariable("isTeamMission", isTeamMission);

                List<String> teammates = ninjaTeam.stream()
                        .filter(teammate -> !teammate.email().equals(ninja.email()))
                        .map(NinjaEmailInfo::name)
                        .toList();
                context.setVariable("teammates", teammates);

                String htmlContent = templateEngine.process("mission-assignment-notification", context);
                helper.setText(htmlContent, true);

                mailSender.send(mimeMessage);
                log.info("Correo de misión de equipo enviado con éxito a: {} ({}) para la misión: {}", ninja.name(), ninja.email(), missionTitle);

            } catch (MessagingException | UnsupportedEncodingException exception){
                log.error("Error al enviar correo de misión de equipo a {} ({}): {}", ninja.name(), ninja.email(), exception.getMessage(), exception);
            }
        }
    }
}
