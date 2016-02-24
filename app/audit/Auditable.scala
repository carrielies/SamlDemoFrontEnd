package audit

import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.audit.model.{Audit, DataEvent}

trait Auditable {

  def appName: String

  def audit: Audit

  def sendDataEvent(transactionName: String, path: String = "N/A",
                    tags: Map[String, String] = Map.empty[String, String],
                    detail: Map[String, String], eventType: String)
                   (implicit hc: HeaderCarrier) =
    audit.sendDataEvent(DataEvent(appName, auditType = eventType,
      tags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(transactionName, path) ++ tags,
      detail = AuditExtensions.auditHeaderCarrier(hc).toAuditDetails(detail.toSeq: _*)))

}
