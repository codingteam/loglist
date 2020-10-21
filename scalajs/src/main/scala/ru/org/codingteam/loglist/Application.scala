package ru.org.codingteam.loglist

import io.circe.generic.auto._
import io.circe.parser.decode
import org.scalajs.dom
import org.scalajs.dom.ext.{Ajax, _}
import org.scalajs.dom.{Element, Event}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object Application {

  def voteHandler(action: String, id: String, ratingContainer: Element)(event: Event) = {
    for {
      xhr <- Ajax.post(s"/api/quote/$id/$action")
      response <- decode[QuoteRating](xhr.responseText)
    } {
      ratingContainer.textContent = response.rating.toString
    }
  }

  def fillSuggestedQuoteCounters() = {
    for {
      xhr <- Ajax.get(s"/api/quote/count/suggested")
      response <- decode[QuoteCount](xhr.responseText)
    } {
      dom.document.querySelectorAll(".suggested-quote-counter").map { node =>
        node.textContent = response.count.toString
      }
    }
  }

  def main(args: Array[String]): Unit = {
    dom.window.onload = { event: Event =>
      dom.document.querySelectorAll(".quote-rating").map { node =>
        val id = node.attributes.getNamedItem("data-id").value

        val element = node.asInstanceOf[Element]
        val rating = element.querySelector(".quote-rating-value")
        val plus = element.querySelector(".plus")
        val minus = element.querySelector(".minus")

        plus.addEventListener("click", voteHandler("upvote", id, rating) _)
        minus.addEventListener("click", voteHandler("downvote", id, rating) _)
      }

      fillSuggestedQuoteCounters()
    }
  }

}
