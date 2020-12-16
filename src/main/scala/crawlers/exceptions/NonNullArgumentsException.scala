package crawlers.exceptions

case class NonNullArgumentsException(arguments: String*) extends IllegalArgumentException {
  override def getLocalizedMessage: String = arguments.mkString(",") + "cant be null"
}

case class IllegalArgumentsException(arguments: String*) extends IllegalArgumentException {
  override def getLocalizedMessage: String = arguments.mkString(",") + "type is illegal"
}