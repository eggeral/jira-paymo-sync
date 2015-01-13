package software.egger.jirapaymosync

import java.net.URI
import java.util.logging.Logger

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import org.rogach.scallop.ScallopConf

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
  val paymoApiUri  =  "https://app.paymoapp.com/api/"


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
      val factory = new AsynchronousJiraRestClientFactory()
      val uri = new URI(conf.jiraUri())
      val jiraUser = conf.jiraUser()

      val client = factory.createWithBasicHttpAuthentication(uri, conf.jiraUser(), conf.jiraPassword())

      val searchPromise = client.getSearchClient.searchJql(s"status not in (Done, Closed) AND (assignee=$jiraUser OR watcher=$jiraUser)")
      val result = searchPromise.claim()
      result.getIssues.foreach(issue => println(issue.getKey))

      val paymoClient = new PaymoClient(conf.paymoUser(), conf.paymoPassword())
      val paymoTaskList = paymoClient.getPaymoTaskList(conf.paymoProjectId())

      paymoTaskList.foreach(println)
      var missingIssues : List[Issue] = List.empty

      for{ issue <- result.getIssues } {
        val filtered = paymoTaskList.filter(task => task.getName.startsWith(issue.getKey + " "))
        if (filtered.size == 0){
          missingIssues = issue :: missingIssues
        }
      }

      println("=========")
      for (missingIssue <- missingIssues) {
        println(s"Creating task in Paymo")
        val taskName = s"${missingIssue.getKey} ${missingIssue.getSummary}"
        println(taskName)
        paymoClient.createTask(conf.paymoTaskListId(), taskName)
      }
      println("=========")
    }
    catch
      {
        case e: Exception => logger.severe("Error: " + e.getMessage)
      }
    logger.info("Done.")
    System.exit(0)

  }



}

