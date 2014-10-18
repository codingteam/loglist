package ru.org.codingteam.loglist

import org.scalajs.dom
import org.scalajs.dom.extensions.Ajax
import org.scalajs.dom.extensions._
import org.scalajs.dom.{Element, Event}

import scala.scalajs.js

object Application extends js.JSApp {

  def voteHandler(action: String, id: String)(event: Event) = {
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
    Ajax.post(s"/quote/$id/$action").onSuccess { case request =>
      val response = upickle.read[QuoteRating](request.responseText)
      dom.console.log(response.rating)
    }
  }

  def main(): Unit = {
    dom.window.onload = { event: Event =>
      dom.document.querySelectorAll(".quote-rating").map { node =>
        val id = node.attributes.getNamedItem("data-id").value

        val element = node.asInstanceOf[Element]
        val plus = element.querySelector(".plus")
        val minus = element.querySelector(".minus")

        plus.addEventListener("click", voteHandler("upvote", id) _)
        minus.addEventListener("click", voteHandler("downvote", id) _)
      }
    }
  }

}
