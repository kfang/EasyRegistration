package com.github.kfang.easyregistration

import akka.actor.ActorSystem

case class AppPackage(
  sys: ActorSystem,
  config: AppConfig,
  db: AppDatabase,
  services: AppServices
)
