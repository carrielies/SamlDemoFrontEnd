package config

import uk.gov.hmrc.http.cache.client.SessionCache
import uk.gov.hmrc.play.audit.http.config.LoadAuditingConfig
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector => Auditing}
import uk.gov.hmrc.play.config.{ServicesConfig, AppName, RunMode}
import uk.gov.hmrc.play.frontend.auth.connectors.{DelegationConnector, AuthConnector}
import uk.gov.hmrc.play.http.{HttpGet, HttpDelete, HttpPut}
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.http.ws.{WSDelete, WSGet, WSPost, WSPut, WSPatch}
import uk.gov.hmrc.play.partials.CachedStaticHtmlPartialRetriever

object SamlFrontendAuditConnector extends Auditing with AppName with RunMode {
  override lazy val auditingConfig = LoadAuditingConfig(s"$env.auditing")
}

object WSHttp extends WSGet with WSPut with WSPost with WSDelete with WSPatch {
  override val hooks = NoneRequired
}

object WSHttpWithAudit extends WSGet with WSPut with WSPost with WSDelete with AppName with HttpAuditing with RunMode{
  override val hooks = Seq(AuditingHook)
  override val auditConnector = SamlFrontendAuditConnector
}


object CachedStaticHtmlPartialProvider extends CachedStaticHtmlPartialRetriever {
  override val httpGet = WSHttp
}

object FrontendAuthConnector extends AuthConnector with ServicesConfig {
  val serviceUrl = baseUrl("auth")
  lazy val http = WSHttp
}

object SamlSessionCache extends SessionCache with AppName with ServicesConfig {
  override lazy val http = WSHttp
  override lazy val defaultSource = appName
  override lazy val baseUri = baseUrl("cachable.session-cache")
  override lazy val domain = getConfString("cachable.session-cache.domain", throw new Exception(s"Could not find config 'cachable.session-cache.domain'"))
}

object FrontendDelegationConnector extends DelegationConnector with ServicesConfig {
  val serviceUrl = baseUrl("delegation")
  lazy val http = WSHttp
}

