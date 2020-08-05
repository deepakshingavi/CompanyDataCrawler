package com.ds.training.http

import java.io.{BufferedWriter, File, FileWriter}

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, fileUpload, onSuccess, parameters, pathPrefix, _}
import akka.http.scaladsl.server.Route
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{FileIO, Sink}
import com.ds.training.CompanyMicroservice
import com.ds.training.model.MatchedCompanyData

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
 * Defines CSV REST endpoints marshaller, routes
 */
trait CsvService {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  def getMatchingCompanyData(companyList: Seq[Map[String, String]], minThreshold: Double): Future[Either[String, Seq[MatchedCompanyData]]] = {
    CompanyMicroservice.companyService.getMatchingCompanyData(companyList, minThreshold)
  }

  /**
   * Defines HTTP routes to access the REST API
   */
  val csvRoutes: Route = {
    pathPrefix("matchingcompanies") {
      (fileUpload("userInputCsv") & parameters("minThreshold".as[Double].?)) {
        case (userInputCsv, minThreshold) =>
          val output = userInputCsv._2
            .via(CsvParsing.lineScanner())
            .via(CsvToMap.toMapAsStrings())
            .runWith(Sink.seq)

          onSuccess(output) { output => {
            complete {
              getMatchingCompanyData(output, minThreshold.getOrElse(0.0)).map[ToResponseMarshallable] {
                case Right(matchedCompanies) =>
                  val fileContent = matchedCompanies.map(t => s"${t.userId},${t.searchCompany},${t.matched_company_name},${t.matched_company_id}").mkString("\n")
                  val file = writeToFile(fileContent)
                  val source = FileIO.fromPath(file.toPath)
                    .watchTermination() { case (_, result) =>
                      result.onComplete(_ => file.delete())
                    }
                  HttpResponse(status = StatusCodes.OK, entity = HttpEntity(ContentTypes.`text/csv(UTF-8)`, source))
                case Left(matchedCompanies) => InternalServerError -> matchedCompanies
              }
            }
          }
          }
      }
    }
  }

  /**
   * Push processed data to a temproray CSV file
   * @param content - String in csv format
   * @return
   */
  def writeToFile(content: String): File = {
    val fileTs = System.nanoTime()
    val tempFolder = CompanyMicroservice.config.getString("services.db.tmpDir")
    val file = new File(s"$tempFolder/matched_companies_$fileTs.csv")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("id,name,matched_company,matched_company_id\n")
    bw.write(content)
    bw.close()
    file
  }
}
