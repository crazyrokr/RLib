package javasabr.rlib.mail.sender.impl;

import static java.util.concurrent.CompletableFuture.runAsync;

import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javasabr.rlib.mail.sender.MailSender;
import javasabr.rlib.mail.sender.MailSenderConfig;
import javasabr.rlib.mail.sender.exception.UncheckedMessagingException;
import lombok.Builder;
import lombok.CustomLog;
import lombok.Getter;

@CustomLog
public class JavaxMailSender implements MailSender {

  @Getter
  @Builder
  public static class JavaxMailSenderConfig {
    private int executorMinThreads;
    private int executorMaxThreads;
    private int executorKeepAlive;
    private Executor executor;
  }

  private final Executor executor;
  private final Session session;
  private final InternetAddress from;

  public JavaxMailSender(MailSenderConfig config) {
    this(config, JavaxMailSenderConfig
        .builder()
        .executorKeepAlive(60)
        .executorMinThreads(1)
        .executorMaxThreads(2)
        .build());
  }

  public JavaxMailSender(MailSenderConfig config, JavaxMailSenderConfig javaxConfig) {

    var prop = new Properties();
    prop.put("mail.smtp.auth", String.valueOf(config.isUseAuth()));
    prop.put("mail.smtp.host", config.getHost());
    prop.put("mail.smtp.port", String.valueOf(config.getPort()));

    if (config.isEnableTls()) {
      prop.put("mail.smtp.socketFactory.port", String.valueOf(config.getPort()));
      prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      prop.put("mail.smtp.starttls.enable", "true");
      prop.put("mail.smtp.ssl.trust", config.getSslHost());
    }

    var username = config.getUsername();
    var password = config.getPassword();

    this.session = Session.getInstance(
        prop, new Authenticator() {

          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        });
    try {
      this.from = new InternetAddress(config.getFrom());
    } catch (AddressException e) {
      throw new RuntimeException(e);
    }

    log.info("Initialized javax mail sender with settings:");
    log.info(username, "User:[%s]"::formatted);
    log.info(config.getFrom(), "From:[%s]"::formatted);
    log.info(config.getHost(), config.getPort(), "Server:[%s:%d]"::formatted);
    log.info(config.isEnableTls(), "Using SSL:[%s]"::formatted);

    if (javaxConfig.getExecutor() != null) {
      this.executor = javaxConfig.getExecutor();
    } else {
      this.executor = new ThreadPoolExecutor(
          javaxConfig.getExecutorMinThreads(),
          javaxConfig.getExecutorMaxThreads(),
          javaxConfig.getExecutorKeepAlive(),
          TimeUnit.SECONDS,
          new SynchronousQueue<>(),
          Executors.defaultThreadFactory(),
          new ThreadPoolExecutor.CallerRunsPolicy());
    }
  }

  @Override
  public void send(String email, String subject, String content) {
    try {
      var mimeBodyPart = new MimeBodyPart();
      mimeBodyPart.setContent(content, "text/html; charset=UTF-8");
      var multipart = new MimeMultipart();
      multipart.addBodyPart(mimeBodyPart);

      var message = new MimeMessage(session);
      message.setFrom(from);
      message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(email));
      message.setSubject(subject, StandardCharsets.UTF_8.name());
      message.setContent(multipart);
      Transport.send(message);
    } catch (MessagingException e) {
      throw new UncheckedMessagingException(e);
    }
  }

  @Override
  public CompletableFuture<Void> sendAsync(
      String email,
      String subject,
      String content) {
    return runAsync(() -> send(email, subject, content), executor);
  }
}
