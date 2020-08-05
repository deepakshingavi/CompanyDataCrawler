package com.ds.training.model

/**
 * Response object for bulk user keyword matching service
 * @param userId
 * @param searchCompany
 * @param matched_company_name
 * @param matched_company_id
 */
case class MatchedCompanyData(userId: String, searchCompany: String, matched_company_name: String = ""
                              , matched_company_id: String = "")
