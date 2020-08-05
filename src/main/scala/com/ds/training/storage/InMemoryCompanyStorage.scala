package com.ds.training.storage

import java.nio.charset.{Charset, CharsetDecoder, CodingErrorAction}

import akka.event.{Logging, LoggingAdapter}
import com.ds.training.CompanyMicroservice
import com.ds.training.CompanyMicroservice.system

import scala.io.BufferedSource

/**
 * Object which loads to company csv data.
 * This is passed to Matcher.matchCompanies as a datastore
 */
object InMemoryCompanyStorage {

  val logger: LoggingAdapter = Logging(system, getClass)
  logger.info(s"Start  InMemoryCompanyStorage")

  /**
   * Ignore any non UTF-8 characters
   * Found a charcter on
   */
  val decoder: CharsetDecoder = Charset.forName("UTF-8").newDecoder
  decoder.onMalformedInput(CodingErrorAction.IGNORE)
  private val filePath: String = CompanyMicroservice.config.getString("services.db.companyFilePath")
  val bufferedSource: BufferedSource = io.Source.fromFile(filePath)(decoder)

  logger.info(s"Loaded  the file")
  val companyMapData: List[Map[String, String]] = bufferedSource.getLines.drop(1).map(record => {
    val cols = record.split(",").map(_.trim)
    Map("id" -> cols(0), "name" -> cols(1))
  }).toList
  bufferedSource.close()
  logger.info(s"Finish InMemoryCompanyStorage")

}
