package views.fields

import views.html.fields.FieldConstructorTemplate

object FieldHelpers {
  import views.html.helper.FieldConstructor
  implicit val fields = FieldConstructor(FieldConstructorTemplate.render)
}
