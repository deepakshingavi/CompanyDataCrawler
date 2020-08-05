package com.ds.training


import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.ds.training.http.{CsvService, JsonService}
import com.ds.training.service.CompanyService
import com.ds.training.storage.LoadCompanyStorage
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
 * Common object use to define all necesary object,
 * -> Akka system
 * -> Execution context
 * -> Application Cofiguration
 * -> Create Company data store
 * -> Create Company service
 * -> Defines the http binder
 */
object CompanyMicroservice extends JsonService with CsvService {
  override implicit val system: ActorSystem = ActorSystem()
  override implicit val executor: ExecutionContextExecutor = system.dispatcher

  override val config: Config = ConfigFactory.load()
  override val logger: LoggingAdapter = Logging(system, getClass)

  private val companyStorage: LoadCompanyStorage = LoadCompanyStorage()
  val companyService: CompanyService = CompanyService(companyStorage)

  def httpBinder: Future[Http.ServerBinding] = {
    Http().bindAndHandle(csvRoutes ~ jsonRoutes, config.getString("http.interface"), config.getInt("http.port"))
  }
}

/**
 * Application object to Start the webserver by invoking httpBinder
 */
object CompanyMicroserviceRun extends App {
  CompanyMicroservice.httpBinder
}
