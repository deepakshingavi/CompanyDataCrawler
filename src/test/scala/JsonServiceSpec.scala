import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.event.NoLogging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, Multipart}
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.ds.training.model.Matcher
import com.ds.training.http.{CsvService, JsonService}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.FiniteDuration
import scala.io.Source


class JsonServiceSpec extends AsyncFlatSpec with Matchers with ScalatestRouteTest with JsonService with CsvService {
  override def testConfigSource = "akka.loglevel = WARNING"

  override def config = testConfig

  override val logger = NoLogging

  /*override lazy val companyApiConnectionFlow = Flow[HttpRequest].map { request =>
    if (request.uri.toString().endsWith("minThreshold=0.5"))
      HttpResponse(status = OK, entity = marshal(companyInfo))
    else
      HttpResponse(status = BadRequest, entity = marshal("Bad ip format"))
  }*/

  "Company Service" should "respond to single query" in {
    val companyInfo = Matcher.ScoredMatch("Jesus House", "4047907", 0.5)
    Get(s"/company?name=Jesus&minThreshold=0.5") ~> jsonRoutes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[Matcher.ScoredMatch] shouldBe companyInfo
    }
  }

  "Company Service" should "return empty" in {
    Get(s"/company?name=Jesus&minThreshold=1.0") ~> jsonRoutes ~> check {
      status shouldBe NotFound
      contentType shouldBe `text/plain(UTF-8)`
      responseAs[String] shouldBe "No matching entries found."
    }
  }

  "Company Service" should " exact company matches " in {

    implicit def default(implicit system: ActorSystem) = RouteTestTimeout(FiniteDuration.apply(5, TimeUnit.SECONDS))

    val inputFile = new File("src/main/resources/sample_user_records.csv")
    val outputFile = new File("src/main/resources/sample_user_output_exact_match.csv")
    val outputFileConn = Source.fromFile(outputFile)
    val outputFileContent = outputFileConn.getLines().mkString("\n")
    outputFileConn.close()

    val formData = Multipart.FormData.fromFile("userInputCsv", ContentTypes.`application/octet-stream`, inputFile, 100000)
    Post("/matchingcompanies?minThreshold=1.0", formData) ~> csvRoutes ~> check {
      status shouldBe OK
      contentType shouldBe `text/csv(UTF-8)`
      responseAs[String] shouldBe outputFileContent
    }
  }

  "Company Service" should " first best company match " in {

    implicit def default(implicit system: ActorSystem) = RouteTestTimeout(FiniteDuration.apply(5, TimeUnit.SECONDS))

    val inputFile = new File("src/main/resources/sample_user_records.csv")
    val outputFile = new File("src/main/resources/sample_user_output_best_match.csv")
    val outputFileConn = Source.fromFile(outputFile)
    val outputFileContent = outputFileConn.getLines().mkString("\n")
    outputFileConn.close()

    val formData = Multipart.FormData.fromFile("userInputCsv", ContentTypes.`application/octet-stream`, inputFile, 100000)
    Post("/matchingcompanies?minThreshold=0.0", formData) ~> csvRoutes ~> check {
      status shouldBe OK
      contentType shouldBe `text/csv(UTF-8)`
      responseAs[String] shouldBe outputFileContent
    }
  }

  "Company Service" should " default company match " in {

    implicit def default(implicit system: ActorSystem) = RouteTestTimeout(FiniteDuration.apply(5, TimeUnit.SECONDS))

    val inputFile = new File("src/main/resources/sample_user_records.csv")
    val outputFile = new File("src/main/resources/sample_user_output_best_match.csv")
    val outputFileConn = Source.fromFile(outputFile)
    val outputFileContent = outputFileConn.getLines().mkString("\n")
    outputFileConn.close()

    val formData = Multipart.FormData.fromFile("userInputCsv", ContentTypes.`application/octet-stream`, inputFile, 100000)
    Post("/matchingcompanies", formData) ~> csvRoutes ~> check {
      status shouldBe OK
      contentType shouldBe `text/csv(UTF-8)`
      responseAs[String] shouldBe outputFileContent
    }
  }

}
