package builders

import org.mockito.Matchers
import org.mockito.Mockito._
import uk.gov.hmrc.domain._
import uk.gov.hmrc.play.frontend.auth._
import uk.gov.hmrc.play.frontend.auth.connectors.AuthConnector
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{AtedAccount, OrgAccount, PayeAccount, _}

import scala.concurrent.Future

object AuthBuilder {

  def createUserAuthContext(userId: String, userName: String): AuthContext = {
    AuthContext(authority = createUserAuthority(userId), nameFromSession = Some(userName))
  }

  def createAgentAuthContext(userId: String, userName: String, agentRefNo : Option[String]=None): AuthContext = {
    AuthContext(authority = createAgentAuthority(userId, AgentAdmin, agentRefNo), nameFromSession = Some(userName))
  }

  def createDelegatedAuthContext(userId: String, userName: String): AuthContext = {
    val user = new AuthContext(
      user = LoggedInUser(
        userId = userId,
        loggedInAt = None,
        previouslyLoggedInAt = None,
        governmentGatewayToken = None,
        credentialStrength = CredentialStrength.Weak,
        confidenceLevel = ConfidenceLevel.L50),
      principal = Principal(
        name = Some(userName),
        accounts = Accounts(ated = Some(AtedAccount("ated/XN1200000100001", AtedUtr("XN1200000100001"))))),
      attorney = Some(Attorney(
        name = userName,
        returnLink = Link(url = "https://www.tax.service.gov.uk", text = "return"))))
    user
  }

  def createAgentAssistantAuthContext(userId: String, userName: String, agentRefNo : Option[String]=None): AuthContext = {
    AuthContext(authority = createAgentAuthority(userId, AgentAssistant, agentRefNo), nameFromSession = Some(userName))
  }

  def createInvalidAuthContext(userId: String, userName: String): AuthContext = {
    AuthContext(authority = createInvalidAuthority(userId), nameFromSession = Some(userName))
  }

  def mockAuthorisedUser(userId: String, mockAuthConnector: AuthConnector) {
    when(mockAuthConnector.currentAuthority(Matchers.any())) thenReturn {
      Future.successful(Some(createUserAuthority(userId)))
    }
  }

    def mockUnsubscribedUser(userId: String, mockAuthConnector: AuthConnector) {
    when(mockAuthConnector.currentAuthority(Matchers.any())) thenReturn {
      Future.successful(Some(createUnsubscribedUserAuthority(userId)))
    }
  }

  def mockAuthorisedAgent(userId: String, mockAuthConnector: AuthConnector) {
    when(mockAuthConnector.currentAuthority(Matchers.any())) thenReturn {
      Future.successful(Some(createAgentAuthority(userId, AgentAdmin, Some("JARN1234567"))))
    }
  }

  def mockUnsubscribedAgent(userId: String, mockAuthConnector: AuthConnector) {
    when(mockAuthConnector.currentAuthority(Matchers.any())) thenReturn {
      Future.successful(Some(createAgentAuthority(userId, AgentAdmin, None)))
    }
  }

  def mockAuthorisedAgentAssistant(userId: String, mockAuthConnector: AuthConnector) {
    when(mockAuthConnector.currentAuthority(Matchers.any())) thenReturn {
      Future.successful(Some(createAgentAuthority(userId, AgentAssistant, Some("JARN1234567"))))
    }
  }


  def mockUnAuthorisedUser(userId: String, mockAuthConnector: AuthConnector) {
    when(mockAuthConnector.currentAuthority(Matchers.any())) thenReturn {
      val payeAuthority = Authority(userId, Accounts(paye = Some(PayeAccount(userId, Nino("AA026813B")))),
        None, None, CredentialStrength.Weak, ConfidenceLevel.L50)

      Future.successful(Some(payeAuthority))
    }
  }

  private def createInvalidAuthority(userId: String): Authority = {
    Authority(userId, Accounts(paye = Some(PayeAccount("paye/AA026813", Nino("AA026813B")))), None, None, CredentialStrength.Weak, ConfidenceLevel.L50)
  }

  private def createUserAuthority(userId: String): Authority = {
    Authority(userId, Accounts(org= Some(OrgAccount("org/123", Org("123"))), ated = Some(AtedAccount("ated/XN1200000100001", AtedUtr("XN1200000100001")))),
      None, None, CredentialStrength.Weak, ConfidenceLevel.L50)
  }

  private def createUnsubscribedUserAuthority(userId: String): Authority = {
    Authority(userId, Accounts(org= Some(OrgAccount("org/123", Org("123")))), None, None, CredentialStrength.Weak, ConfidenceLevel.L50)
  }


  private def createAgentAuthority(userId: String, agentRole: AgentRole, agentRefNo : Option[String] = None): Authority = {
    val agentCode = "AGENT-123"
    val agentBusinessUtr = agentRefNo.map{agentRef =>
      AgentBusinessUtr(agentRef)
    }

    val agentAccount = AgentAccount(link = s"agent/$agentCode",
      agentCode = AgentCode(agentCode),
      agentUserId = AgentUserId(userId),
      agentUserRole = agentRole,
      payeReference = None,
      agentBusinessUtr = agentBusinessUtr)
    Authority(userId, Accounts(agent = Some(agentAccount)), None, None, CredentialStrength.Weak, ConfidenceLevel.L50)
  }
}
