package com.ds.training.service

import com.ds.training.model.{MatchedCompanyData, Matcher}
import com.ds.training.storage.LoadCompanyStorage

import scala.concurrent.{ExecutionContext, Future}

/**
 * Service class for dealing with http request and data base operations
 * All transactional operations can go here
 *
 * @param companyStorage - Object to interact with database
 * @param executionContext - Execution context of Akka system
 */
case class CompanyService (
                       companyStorage: LoadCompanyStorage
                     )(implicit executionContext: ExecutionContext) {

  /**
   *  Get ths user input and queries the data base to get the matching list of records
   * @param companyList - list of company to be searched
   * @param minThreshold - minimum threshold value
   * @return - Return error message or Matching sequence of company data
   */
  def getMatchingCompanyData(companyList: Seq[Map[String, String]],minThreshold:Double): Future[Either[String,Seq[MatchedCompanyData]]] = {
    companyStorage.getMatchingCompanyData(companyList,minThreshold)
  }


  /**
   * matches the single company name against the database and fetches the best matching resukt
   * @param name - key word to be searched
   * @param minThreshold - minimum threshold value
   * @return - Return error message or Matching a company data
   */
  def getCompany(name: String,minThreshold: Double): Future[Either[String, Matcher.ScoredMatch]] = {
    companyStorage.getCompany(name,minThreshold)
  }

}
