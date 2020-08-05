package com.ds.training.model

object Matcher {

  /**
   * Capture the matched company info with score
   * @param name - Company name
   * @param id - Company identifier
   * @param score - Matching score
   */
  case class ScoredMatch(name: String, id: String, score: Double)

  /** @param name        : name provided in the user uploaded record.
   * @param companies    : List of company dictionaries to match to. For simplicity you can pass the full
   *                     list of companies provided.
   * @param minThreshold : minimum score for acceptable match, 0 <= minThreshold <= 1
   * @return: Option class for a ScoredMatch, populated with the highest scoring match found above minThreshold, None otherwise
   * */
  def matchCompanies(name: String, companies: List[Map[String, String]], minThreshold: Double=0.0): Option[ScoredMatch] = {
    val recordNameTokens = name.toUpperCase.split(" ").toSet
    val scoredMatches = companies.map(c => {
      val tokens = c("name").toUpperCase.split(" ").toSet
      // Score companies based on Jaccard similarity
      val score = tokens.intersect(recordNameTokens).size / tokens.union(recordNameTokens).size.doubleValue
      ScoredMatch(c("name"), c("id"), score)
    })

    // Pick highest score match (if any)
    scoredMatches.filter(_.score >= minThreshold).sortBy(_.score).reverse.headOption
  }

  /**
   * Matches and creates list of ScoredMatch based on the input keywords
   * @param name        : name provided in the user uploaded record.
   * @param companies    : Mapping of words to List of companies with matching keywords.
   * @param minThreshold : minimum score for acceptable match, 0 <= minThreshold <= 1
   * @return: Option class for a ScoredMatch, populated with the highest scoring match found above minThreshold, None otherwise
   * */
  def matchCompaniesImproved(name: String, companies: Map[String,List[Company]], minThreshold: Double=0.0): Option[ScoredMatch] = {
    val recordNameTokens = name.toUpperCase.split(" ").toSet

    val scoredMatches : List[ScoredMatch] = recordNameTokens.flatMap(token => {
      companies.getOrElse(token,List.empty)
    }).map( c => {
      val tokens = c.name.toUpperCase.split(" ").toSet
      // Score companies based on Jaccard similarity
      val score = tokens.intersect(recordNameTokens).size / tokens.union(recordNameTokens).size.doubleValue
      ScoredMatch(c.name, c.id, score)
    }).toList

    // Pick highest score match (if any)
    scoredMatches.filter(_.score >= minThreshold).sortBy(_.score).reverse.headOption
  }

  case class Company(id : String,name : String)


}
