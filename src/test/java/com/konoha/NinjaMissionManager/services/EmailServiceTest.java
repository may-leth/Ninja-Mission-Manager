package com.konoha.NinjaMissionManager.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaEmailInfo;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for EmailService")
public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    @InjectMocks
    private EmailService emailService;

    private static final String FROM_EMAIL = "test@konoha.com";
    private static final String FROM_NAME = "Test Ninja Team";
    private static final String DASHBOARD_URL = "http://localhost:8080/test-dashboard";

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
        ReflectionTestUtils.setField(emailService, "fromName", FROM_NAME);
        ReflectionTestUtils.setField(emailService, "dashboardUrl", DASHBOARD_URL);
    }

    @Nested
    @DisplayName("sendNinjaWelcomeEmail")
    class sendNinjaWelcomeEmailTests {
        @Test
        @DisplayName("Should send welcome email succesfully")
        void shouldSendWelcomeEmailSuccessfully() throws Exception{
            String toEmail = "naruto@gmail.com";
            String ninjaName = "Naruto Uzumaki";
            String village = "Konoha";
            String htmlContent = "<html><body>Welcome Naruto!</body></html>";

            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(eq("welcome-notification"), any(Context.class))).thenReturn(htmlContent);
            doNothing().when(mailSender).send(mimeMessage);

            assertThatNoException().isThrownBy(() -> emailService.sendNinjaWelcomeEmail(toEmail, ninjaName, village));

            verify(mailSender).createMimeMessage();
            verify(templateEngine).process(eq("welcome-notification"), any(Context.class));
            verify(mailSender).send(mimeMessage);
        }

        @Test
        @DisplayName("Should handle MailSendException gracefully")
        void shouldHandleMessagingExceptionGracefully() throws Exception{
            String toEmail = "invalid@email.com";
            String ninjaName = "Test Ninja";
            String village = "Test village";

            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(anyString(), any(Context.class))).thenReturn("test content");
            doThrow(new MailSendException("Email send failed")).when(mailSender).send(mimeMessage);

            assertThatNoException().isThrownBy(() -> emailService.sendNinjaWelcomeEmail(toEmail, ninjaName, village));

            verify(mailSender).createMimeMessage();
            verify(mailSender).send(mimeMessage);
        }

        @Test
        @DisplayName("Should set correct template variables")
        void shouldSetCorrectTemplateVariables() throws Exception{
            String toEmail = "sasuke@gmail.com";
            String ninjaName = "Sasuke Uchiha";
            String village = "Konoha";

            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(anyString(), any(Context.class))).thenReturn("test");

            emailService.sendNinjaWelcomeEmail(toEmail, ninjaName, village);

            verify(templateEngine).process(eq("welcome-notification"), argThat(context -> {
                Context ctx = (Context) context;
                return ninjaName.equals(ctx.getVariable("ninjaName")) &&
                        village.equals(ctx.getVariable("village")) &&
                        DASHBOARD_URL.equals(ctx.getVariable("dashboardUrl"));
            }));
        }
    }

    @Nested
    @DisplayName("sendMissionAssignmentEmail")
    class SendMissionAssignmentEmailTest {
        private NinjaEmailInfo naruto;
        private NinjaEmailInfo sasuke;
        private NinjaEmailInfo sakura;

        @Captor
        private ArgumentCaptor<Context> contextCaptor;

        @BeforeEach
        void setUp(){
            naruto = new NinjaEmailInfo("naruto@gmail.com", "Naruto Uzumaki");
            sasuke = new NinjaEmailInfo("sasuke@gmail.com", "Sasuke Uchiha");
            sakura = new NinjaEmailInfo("sakura@gmail.com", "Sakura Haruno");
        }

        @Test
        @DisplayName("Should send mission assignment email to single ninja successfully")
        void shouldSendMissionAssignmentEmailToSingleNinja() throws Exception{
            List<NinjaEmailInfo> singleNinja = List.of(naruto);
            String missionTitle = "Rescue Mission";
            String missionDescription = "Rescue the princess";
            String missionRank = "A";
            String htmlContent = "<html><body>Mission Assigned!</body></html>";

            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(eq("mission-assignment-notification"), any(Context.class))).thenReturn(htmlContent);
            doNothing().when(mailSender).send(mimeMessage);

            assertThatNoException().isThrownBy(() -> emailService.sendMissionAssignmentEmail(singleNinja, missionTitle, missionDescription, missionRank));

            verify(mailSender, times(1)).createMimeMessage();
            verify(templateEngine, times(1)).process(eq("mission-assignment-notification"), any(Context.class));
            verify(mailSender, times(1)).send(mimeMessage);
        }

        @Test
        @DisplayName("Should send mission assignment emails to multiple ninjas (team mission)")
        void shouldSendMissionAssignmentEmailToTeam() throws Exception {
            List<NinjaEmailInfo> team = List.of(naruto, sasuke, sakura);
            String missionTitle = "Team Mission";
            String missionDescription = "Infiltrate enemy base";
            String missionRank = "B";

            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(eq("mission-assignment-notification"), any(Context.class))).thenReturn("test");

            emailService.sendMissionAssignmentEmail(team, missionTitle, missionDescription, missionRank);

            verify(mailSender, times(3)).createMimeMessage();
            verify(templateEngine, times(3)).process(eq("mission-assignment-notification"), any(Context.class));
            verify(mailSender, times(3)).send(mimeMessage);
        }

        @Test
        @DisplayName("Should handle empty ninja team gracefully")
        void shouldHandleEmptyNinjaTeamGracefully() {
            List<NinjaEmailInfo> emptyTeam = Collections.emptyList();
            String missionTitle = "Empty Mission";
            String missionDescription = "No one to assign";
            String missionRank = "D";

            assertThatNoException().isThrownBy(() -> emailService.sendMissionAssignmentEmail(emptyTeam, missionTitle, missionDescription, missionRank));

            verifyNoInteractions(mailSender);
            verifyNoInteractions(templateEngine);
        }

        @Test
        @DisplayName("Should handle null ninja team gracefully")
        void shouldHandleNullNinjaTeamGracefully() {
            String missionTitle = "Null Mission";
            String missionDescription = "Null team";
            String missionRank = "D";

            assertThatNoException().isThrownBy(() -> emailService.sendMissionAssignmentEmail(null, missionTitle, missionDescription, missionRank));

            verifyNoInteractions(mailSender);
            verifyNoInteractions(templateEngine);
        }

        @Test
        @DisplayName("Should set correct subject for single ninja mission")
        void shouldSetCorrectSubjectForSingleNinjaMission() throws Exception{
            List<NinjaEmailInfo> singleNinja = List.of(naruto);
            String missionTitle = "Solo Mission";

            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(anyString(), any(Context.class))).thenReturn("test");

            emailService.sendMissionAssignmentEmail(singleNinja, missionTitle, "description", "A");

            verify(templateEngine).process(eq("mission-assignment-notification"), argThat(context -> {
                Context ctx = (Context) context;
                return Boolean.FALSE.equals(ctx.getVariable("isTeamMission"));
            }));
        }

        @Test
        @DisplayName("Should set correct subject for team mission")
        void shouldSetCorrectSubjectForTeamMission() throws Exception{
            List<NinjaEmailInfo> team = List.of(naruto, sasuke);
            String missionTitle = "Team Mission";

            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(anyString(), any(Context.class))).thenReturn("test");

            emailService.sendMissionAssignmentEmail(team, missionTitle, "description", "S");

            verify(templateEngine, times(2)).process(eq("mission-assignment-notification"), argThat(context -> {
                Context ctx = (Context) context;
                return Boolean.TRUE.equals(ctx.getVariable("isTeamMission"));
            }));
        }

        @Test
        @DisplayName("Should set correct teammates for each ninja in team mission")
        void shouldSetCorrectTeammatesForEachNinja() throws Exception {
            List<NinjaEmailInfo> team = List.of(naruto, sasuke, sakura);
            String missionTitle = "Team Mission";

            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(anyString(), any(Context.class))).thenReturn("test");

            emailService.sendMissionAssignmentEmail(team, missionTitle, "description", "A");

            verify(templateEngine, times(3)).process(eq("mission-assignment-notification"), contextCaptor.capture());

            List<Context> capturedContexts = contextCaptor.getAllValues();

            Context narutoContext = capturedContexts.get(0);
            assertThat(narutoContext.getVariable("ninjaName")).isEqualTo("Naruto Uzumaki");
            @SuppressWarnings("unchecked")
            List<String> narutoTeammates = (List<String>) narutoContext.getVariable("teammates");
            assertThat(narutoTeammates)
                    .hasSize(2)
                    .containsExactlyInAnyOrder("Sasuke Uchiha", "Sakura Haruno");

            Context sasukeContext = capturedContexts.get(1);
            assertThat(sasukeContext.getVariable("ninjaName")).isEqualTo("Sasuke Uchiha");
            @SuppressWarnings("unchecked")
            List<String> sasukeTeammates = (List<String>) sasukeContext.getVariable("teammates");
            assertThat(sasukeTeammates)
                    .hasSize(2)
                    .containsExactlyInAnyOrder("Naruto Uzumaki", "Sakura Haruno");

            Context sakuraContext = capturedContexts.get(2);
            assertThat(sakuraContext.getVariable("ninjaName")).isEqualTo("Sakura Haruno");
            @SuppressWarnings("unchecked")
            List<String> sakuraTeammates = (List<String>) sakuraContext.getVariable("teammates");
            assertThat(sakuraTeammates)
                    .hasSize(2)
                    .containsExactlyInAnyOrder("Naruto Uzumaki", "Sasuke Uchiha");

            verify(mailSender, times(3)).send(any(MimeMessage.class));
        }

        @Test
        @DisplayName("Should handle MailSendException for one ninja and continue with others")
        void shouldHandleMailSendExceptionAndContinue() throws Exception {
            List<NinjaEmailInfo> team = List.of(naruto, sasuke);

            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(anyString(), any(Context.class))).thenReturn("test");

            doThrow(new MailSendException("Failed"))
                    .doNothing()
                    .when(mailSender).send(mimeMessage);

            assertThatNoException().isThrownBy(() ->
                    emailService.sendMissionAssignmentEmail(team, "Mission", "Description", "B")
            );

            verify(mailSender, times(2)).send(mimeMessage);
        }

        @Test
        @DisplayName("Should set correct mission variables in template context")
        void shouldSetCorrectMissionVariablesInTemplateContext() throws Exception {
            List<NinjaEmailInfo> team = List.of(naruto);
            String missionTitle = "Test Mission";
            String missionDescription = "Test Description";
            String missionRank = "C";

            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(templateEngine.process(anyString(), any(Context.class))).thenReturn("test");

            emailService.sendMissionAssignmentEmail(team, missionTitle, missionDescription, missionRank);

            verify(templateEngine).process(eq("mission-assignment-notification"), argThat(context -> {
                Context ctx = (Context) context;
                return missionTitle.equals(ctx.getVariable("missionTitle")) &&
                        missionDescription.equals(ctx.getVariable("missionDescription")) &&
                        missionRank.equals(ctx.getVariable("missionRank")) &&
                        DASHBOARD_URL.equals(ctx.getVariable("dashboardUrl"));
            }));
        }
    }
}