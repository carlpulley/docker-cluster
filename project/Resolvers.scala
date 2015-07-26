import sbt._

object Resolvers {

  lazy val resolvers = Seq(
    Resolver.defaultLocal,
    Resolver.mavenLocal,
    Resolver.typesafeRepo("releases"),
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
  )

}
