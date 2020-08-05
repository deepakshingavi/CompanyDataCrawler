package com.ds.training.http

import com.ds.training.model.Matcher
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
 * Defines implicit JSON SerDe for the HTTP request and responses
 */
trait MarshUnmarsh extends DefaultJsonProtocol {
  implicit val matchScoredFormat: RootJsonFormat[Matcher.ScoredMatch] = jsonFormat3(Matcher.ScoredMatch.apply)
}
