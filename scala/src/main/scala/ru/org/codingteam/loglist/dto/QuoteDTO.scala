package ru.org.codingteam.loglist.dto

case class QuoteDTO(id: Long, source: String, sourceUrl: Option[String], time: Long, content: String, rating: Int)
