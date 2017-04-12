package com.github.kfang.easyregistration

import akka.actor.{ActorRef, ActorSystem}
import com.github.kfang.easyregistration.services.EmailService

case class AppServices(config: AppConfig)(implicit sys: ActorSystem) {

  val emailService: ActorRef = EmailService.start(config)

}
