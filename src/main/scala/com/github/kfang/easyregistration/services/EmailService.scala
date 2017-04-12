package com.github.kfang.easyregistration.services

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.github.kfang.easyregistration.AppConfig
import com.github.kfang.easyregistration.services.EmailService.Email
import org.apache.commons.mail.SimpleEmail

import scala.util.{Failure, Success, Try}

class EmailService(config: AppConfig) extends Actor with ActorLogging {

  def sendSimpleEmail(e: Email): Unit = {
    val email = new SimpleEmail()
    email.addTo(e.recipient)
    email.setMsg(e.message)
    email.setSubject(e.subject)
    email.setStartTLSEnabled(true)
    email.setStartTLSRequired(true)
    email.setSmtpPort(587)
    email.setFrom(config.SMTP_USERNAME)
    email.setAuthentication(config.SMTP_USERNAME, config.SMTP_PASSWORD)
    email.setHostName(config.SMTP_HOSTNAME)

    Try(email.send()) match {
      case Success(mid) => log.debug(s"sent email: $mid")
      case Failure(err) => log.error(err, "Email Service Error")
    }
  }

  def receive: Receive = {
    case msg: Email => sendSimpleEmail(msg)
  }
}

object EmailService {
  val NAME = "email-service"
  case class Email(recipient: String, subject: String, message: String)

  def start(config: AppConfig)(implicit sys: ActorSystem): ActorRef = {
    sys.actorOf(Props(classOf[EmailService], config), NAME)
  }
}
