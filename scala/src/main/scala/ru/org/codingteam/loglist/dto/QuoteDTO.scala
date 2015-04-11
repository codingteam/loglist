package ru.org.codingteam.loglist.dto

case class QuoteDTO(id: Long, time: Long, content: Option[String], rating: Int)
