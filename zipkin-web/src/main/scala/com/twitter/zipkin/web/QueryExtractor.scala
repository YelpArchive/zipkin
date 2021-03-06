/*
 * Copyright 2012 Twitter Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.zipkin.web

import com.twitter.finagle.httpx.Request
import com.twitter.util.Time

import scala.collection.mutable

class QueryExtractor(defaultQueryLimit: Int) {

  def getLimitStr(req: Request): String = {
    req.params.get("limit").map(_.toInt).getOrElse(defaultQueryLimit).toString
  }

  def getTimestampStr(req: Request): String = {
    req.params.getLong("timestamp").getOrElse(Time.now.inMicroseconds).toString
  }

  def getAnnotations(req: Request): Option[(Seq[String], Map[String, String])] =
    req.params.get("annotationQuery") map { query =>
      val anns = mutable.Buffer[String]()
      val binAnns = mutable.Map[String, String]()

      query.split(" and ") foreach { ann =>
        ann.split("=").toList match {
          case "" :: Nil =>
          case key :: value :: Nil =>
            binAnns += key -> value
          case key :: Nil =>
            anns += key
          case _ =>
        }
      }
      (anns.toSeq, binAnns.toMap)
    }
}
