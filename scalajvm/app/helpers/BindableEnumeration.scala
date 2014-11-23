package helpers

import play.api.mvc.QueryStringBindable

abstract class BindableEnumeration extends Enumeration { Self =>
  implicit def bindable(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[Self.Value] =
    new QueryStringBindable[Self.Value] {
      def bind(key: String, params: Map[String, Seq[String]]) =
        for {
          valueBind <- stringBinder.bind(key, params)
        } yield {
          valueBind match {
            case Right(value) =>
              Self.values.find(_.toString.toLowerCase == value.toLowerCase) match {
                case Some(v) => Right(v)
                case None => Left(s"Unknown parameter type '$value'")
              }
            case other => Left(s"Not found string value for key '$key'")
          }
        }

      def unbind(key: String, value: Self.Value) = stringBinder.unbind(key, value.toString.toLowerCase)
    }
}