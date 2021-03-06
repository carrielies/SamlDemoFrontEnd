credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

val hmrcRepoHost = java.lang.System.getProperty("hmrc.repo.host", "https://nexus-preview.tax.service.gov.uk")

resolvers ++= Seq("hmrc-snapshots" at hmrcRepoHost + "/content/repositories/hmrc-snapshots",
  "hmrc-releases" at hmrcRepoHost + "/content/repositories/hmrc-releases",
  "Central" at hmrcRepoHost + "/content/repositories/central",
  "typesafe-releases" at hmrcRepoHost + "/content/repositories/typesafe-releases",
  "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
  Resolver.url("hmrc-sbt-plugin-releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns))


addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.5")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.8")

addSbtPlugin("uk.gov.hmrc" % "sbt-settings" % "3.2.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "0.10.0")

addSbtPlugin("uk.gov.hmrc" % "hmrc-resolvers" % "0.4.0")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.1.0")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")



