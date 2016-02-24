import sbt.Keys._
import sbt.Tests.{SubProcess, Group}
import sbt._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._

trait MicroService {

  import uk.gov.hmrc._
  import DefaultBuildSettings._
  import uk.gov.hmrc.{SbtBuildInfo, ShellPrompt}
  import scoverage.ScoverageSbtPlugin._
  import TestPhases._

  val appName: String
  val appVersion: String

  lazy val appDependencies : Seq[ModuleID] = ???
  lazy val plugins : Seq[Plugins] = Seq(play.PlayScala)
  lazy val playSettings : Seq[Setting[_]] = Seq.empty

  def makeExcludedFiles(rootDir:File):Seq[String] = {
    val excluded = findPlayConfFiles(rootDir) ++ findSbtFiles(rootDir)
    println(s"[auto-code-review] excluding the following files: ${excluded.mkString(",")}")
    excluded
  }
  def findSbtFiles(rootDir: File): Seq[String] = {
    if(rootDir.getName == "project") {
      rootDir.listFiles().map(_.getName).toSeq
    } else {
      Seq()
    }
  }
  def findPlayConfFiles(rootDir: File): Seq[String] = {
    Option { new File(rootDir, "conf").listFiles() }.fold(Seq[String]()) { confFiles =>
      confFiles
        .map(_.getName.replace(".routes", ".Routes"))
    }
  }
//
//  val wartRemovedExcludedClasses = Seq(
//    "uk.gov.hmrc.BuildInfo",
//    "views.html.helpers.dateInput", "views.html.helpers.atedTextArea", "views.html.helpers.atedInputRadioGroup",
//    "views.html.helpers.atedInput", "views.html.helpers.atedDateFieldsFree", "views.html.helpers.atedSingleCheckBox",
//    "views.html.chargeableProperties.chargeableReturnsConfirmation",
//    "com.kenshoo.play.metrics.ref.ReverseMetricsController", "controllers.reliefs.ref", "controllers.chargeableProperties.ref", "controllers.agents.ref"
//  )

  lazy val scoverageSettings = {
    Seq(
      ScoverageKeys.coverageExcludedPackages :=  "<empty>;Reverse.*;views.*;views.html.*;app.Routes.*;prod.*;uk.gov.hmrc.*;testOnlyDoNotUseInAppConf.*;forms.*;config.*;models.*;",
      ScoverageKeys.coverageMinimum := 100,
      ScoverageKeys.coverageFailOnMinimum := true,
      ScoverageKeys.coverageHighlighting := true,
      parallelExecution in Test := false
    )
  }

  lazy val microservice = Project(appName, file("."))
    .enablePlugins(plugins : _*)
    .settings(playSettings ++ scoverageSettings : _*)
//    .settings(wartremoverSettings : _*)
    .settings(version := appVersion)
    .settings(scalaSettings: _*)
    .settings(publishingSettings: _*)
    .settings(defaultSettings(): _*)
    .settings(
      targetJvm := "jvm-1.8",
      shellPrompt := ShellPrompt(appVersion),
      libraryDependencies ++= appDependencies,
      parallelExecution in Test := false,
      fork in Test := false,
      retrieveManaged := true
//      wartremoverWarnings  in (Compile, compile) ++= Warts.allBut(Wart.NoNeedForMonad, Wart.DefaultArguments),
//      wartremoverErrors in (Compile, compile) ++= Seq.empty,
//      wartremoverExcluded ++= wartRemovedExcludedClasses,
//      wartremoverExcluded ++= makeExcludedFiles(baseDirectory.value) :+ "controllers.ref"
    )
    .settings(Repositories.playPublishingSettings : _*)
    .settings(inConfig(TemplateTest)(Defaults.testSettings): _*)
    .configs(IntegrationTest)
    .settings(inConfig(TemplateItTest)(Defaults.itSettings): _*)
    .settings(
      Keys.fork in IntegrationTest := false,
      unmanagedSourceDirectories in IntegrationTest <<= (baseDirectory in IntegrationTest)(base => Seq(base / "it")),
      addTestReportOption(IntegrationTest, "int-test-reports"),
      testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
      parallelExecution in IntegrationTest := false)
    .settings(SbtBuildInfo(): _*)
    .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
    .settings(resolvers += Resolver.bintrayRepo("hmrc", "releases"))
}

private object TestPhases {

  val allPhases = "tt->test;test->test;test->compile;compile->compile"
  val allItPhases = "tit->it;it->it;it->compile;compile->compile"

  lazy val TemplateTest = config("tt") extend Test
  lazy val TemplateItTest = config("tit") extend IntegrationTest

  def oneForkedJvmPerTest(tests: Seq[TestDefinition]) =
    tests map {
      test => new Group(test.name, Seq(test), SubProcess(ForkOptions(runJVMOptions = Seq("-Dtest.name=" + test.name))))
    }
}

private object Repositories {

  import uk.gov.hmrc._
  import PublishingSettings._
  import NexusPublishing._

  lazy val playPublishingSettings : Seq[sbt.Setting[_]] = sbtrelease.ReleasePlugin.releaseSettings ++ Seq(

    credentials += SbtCredentials,

    publishArtifact in(Compile, packageDoc) := false,
    publishArtifact in(Compile, packageSrc) := false

  ) ++
    publishAllArtefacts ++
    nexusPublishingSettings

}
