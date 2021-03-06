package software.egger.jirapaymosync

import java.lang.Iterable
import java.net.URI
import java.util.logging.Logger

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter
import org.rogach.scallop.ScallopConf
import software.egger.jirapaymosync.TaskIssueImplicits._

import scala.collection.JavaConversions._

class Conf(arguments: Seq[String]) extends ScallopConf(arguments)
{
  val trustStore = opt[String]()
  val jiraUri = opt[String](required = true)
  val jiraUser = opt[String](required = true)
  val jiraPassword = opt[String](required = true)
  val paymoProjectId = opt[String](required = true)
  val paymoTaskListId = opt[String](required = true)
  val paymoUser = opt[String](required = true)
  val paymoPassword = opt[String](required = true)
}

object Main
{

  val logger = Logger.getLogger("software.egger.jirapaymosync.Main")
  val paymoApiUri = "https://app.paymoapp.com/api/"


  def main(args: Array[String])
  {
    val conf = new Conf(args)
    if (conf.trustStore.isDefined)
    {
      val trustStore = conf.trustStore()
      logger.info(s"Setting ssl trust store to: $trustStore.")
      System.setProperty("javax.net.ssl.trustStore", trustStore)
    }

    try
    {
      val jiraIssues = getJiraIssues(conf)
      val paymoClient = new PaymoClient(makePaymoRestClient(conf.paymoUser(), conf.paymoPassword()))
      val paymoTasks = paymoClient.getPaymoTaskList(conf.paymoProjectId())

      val taskMatches = jiraIssues.toList matchWith paymoTasks

      createMissingJiraIssuesInPaymo(paymoClient, conf.paymoTaskListId(), taskMatches)

      //Todo sync work log and time entries
    }
    catch
      {
        case e: Exception => logger.severe("Error: " + e.getMessage)
      }
    logger.info("Done.")
    System.exit(0)

  }

  def createMissingJiraIssuesInPaymo(paymoClient: PaymoClient, taskListId: String, taskMatches: scala.Iterable[TaskMatch])
  {
    for
    {
      taskMatch <- taskMatches
      if taskMatch.jiraIssue.isDefined && taskMatch.paymoTask.isEmpty
    }
    {
      paymoClient.createTask(taskListId, taskMatch.jiraIssue.get)
    }
  }

  def getJiraIssues(conf: Conf): Iterable[Issue] =
  {
    val jiraUri = new URI(conf.jiraUri())
    val jiraUser = conf.jiraUser()

    val factory = new AsynchronousJiraRestClientFactory()
    val jiraClient = factory.createWithBasicHttpAuthentication(jiraUri, jiraUser, conf.jiraPassword())

    val searchPromise = jiraClient.getSearchClient.searchJql(s"status not in (Done, Closed) AND (assignee=$jiraUser OR watcher=$jiraUser)")
    val result = searchPromise.claim()
    result.getIssues
  }

  private def makePaymoRestClient(user: String, password: String): Client =
  {
    val restClient = Client.create()
    restClient.addFilter(new HTTPBasicAuthFilter(user, password))
    restClient
  }

}

