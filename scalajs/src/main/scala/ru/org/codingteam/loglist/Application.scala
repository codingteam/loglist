package ru.org.codingteam.loglist

import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.{Element, Event}
import scala.scalajs.js
import upickle.default._

object Application extends js.JSApp {

  def voteHandler(action: String, id: String, ratingContainer: Element)(event: Event) = {
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
    Ajax.post(s"/quote/$id/$action").onSuccess { case request =>
      val response = read[QuoteRating](request.responseText)
      ratingContainer.textContent = response.rating.toString
    }
  }

  def fillSuggestedQuoteCounters() = {
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
    Ajax.get(s"/quote/count/suggested").onSuccess { case request =>
      val response = read[SuggestedQuoteCount](request.responseText)
      dom.document.querySelectorAll(".suggested-quote-counter").map { node =>
        node.textContent = response.count.toString
      }
    }
  }

  def main(): Unit = {
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
