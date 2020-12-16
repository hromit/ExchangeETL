package crawlers.pipeline

case class S3Pipeline() extends Pipeline{
  override def process[Result](pageResultItem: (String, Result)): Unit = ???
}
