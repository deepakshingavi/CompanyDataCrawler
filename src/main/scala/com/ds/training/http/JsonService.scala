package com.ds.training.http

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ds.training.model.Matcher
import com.ds.training.CompanyMicroservice
import com.typesafe.config.Config

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
 * Defines JSON REST endpoints marshaller, routes
 */
trait JsonService extends MarshUnmarsh {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  def config: Config

  val logger: LoggingAdapter

  def fetchCompanyInfo(name: String, minThreshold: Double): Future[Either[String, Matcher.ScoredMatch]] = {
    CompanyMicroservice.companyService.getCompany(name, minThreshold)
  }

  /**
   * Defines HTTP routes to access sample REST API
   */
  val jsonRoutes: Route = {
    pathPrefix("company") {
      (get & parameters("name".as[String], "minThreshold".as[Double])) { (name, minThreshold) =>
        complete {
          fetchCompanyInfo(name, minThreshold).map[ToResponseMarshallable] {
            case Right(ipInfo) => ipInfo
            case Left(errorMessage) => NotFound -> errorMessage
          }
        }
      }
    }
  }
}
