package com.ds.training.storage

import akka.event.Logging
import com.ds.training.CompanyMicroservice.system
import com.ds.training.model.{MatchedCompanyData, Matcher}

import scala.concurrent.{ExecutionContext, Future}

/**
 * Defines the method for a Storage class
 */
sealed trait CompanyStorage {

  /**
   * Searches of the keywords against the database
   * @param name - keywords to searched
   * @param minThreshold minimum threshold
   * @return
   */
  def getCompany(name: String, minThreshold: Double): Future[Either[String, Matcher.ScoredMatch]]

  /**
   * Searches of the set of keywords against the database and gets back matching records combined with the input.
   * @param companyList - Sequence of keywords to be searched
   * @param minThreshold minimum threshold
   * @return
   */
  def getMatchingCompanyData(companyList: Seq[Map[String, String]],minThreshold:Double=0.0): Future[Either[String,Seq[MatchedCompanyData]]]

}

/**
 * Defines method to interact with database
 *
 * @param executionContext - Akka system execution context
 */
case class LoadCompanyStorage()
                             (implicit executionContext: ExecutionContext)
  extends CompanyStorage {

  val logger = Logging(system, getClass)

  /**
   *
   * @param companyList - Sequence of keywords to be searched
   * @param minThreshold minimum threshold
   * @return
   */
  def getMatchingCompanyData(companyList: Seq[Map[String, String]],minThreshold:Double=0.0): Future[Either[String,Seq[MatchedCompanyData]]] = {
    Future {
      val result : Seq[MatchedCompanyData] = companyList.map(query => {
        val id = query("id")
        val name = query("name")
        Matcher.matchCompanies(name, InMemoryCompanyStorage.companyMapData,minThreshold) match {
          case Some(result) =>
            MatchedCompanyData(id, name, result.name, result.id)
          case None => MatchedCompanyData(id, name)
        }
      })
      if (result.isEmpty) {
        Left("Error occurred while querying Database!!!")
      } else {
        Right(result)
      }
    }
  }

  /**
   * Get matching company name for the inout keywords
   * @param name - keywords to searched
   * @param minThreshold minimum threshold
   * @return Error message or Matched company object
   */
  def getCompany(name: String, minThreshold: Double): Future[Either[String, Matcher.ScoredMatch]] = {
    logger.info(s"input name=${name} minThreshold=${minThreshold}")
    Future {
      Matcher.matchCompanies(name, InMemoryCompanyStorage.companyMapData, minThreshold) match {
        case Some(result) =>
          logger.info(s"result.get=$result")
          Right(result)
        case None => Left("No matching entries found.")
      }
    }
  }

}
