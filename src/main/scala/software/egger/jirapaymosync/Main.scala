package software.egger.jirapaymosync

import java.net.URI
import java.util.logging.Logger
import javax.ws.rs.core.MediaType

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.sun.jersey.api.client.{GenericType, Client}
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter
import org.rogach.scallop.ScallopConf
import software.egger.jirapaymosync.paymo.Tasks

import scala.collection.JavaConversions._

class Conf(arguments: Seq[String]) extends ScallopConf(arguments)
{
  val trustStore = opt[String]()
  val jiraUri = opt[String](required = true)
  val jiraUser = opt[String](required = true)
  val jiraPassword = opt[String](required = true)
  val paymoProjectId = opt[String](required = true)
  val paymoUser = opt[String](required = true)
  val paymoPassword = opt[String](required = true)
}

object Main extends App
{
  private val logger: Logger = Logger.getLogger("software.egger.jirapaymosync.Main")

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

    val client = factory.createWithBasicHttpAuthentication(uri, conf.jiraUser(), conf.jiraPassword())

    val searchPromise = client.getSearchClient.searchJql("status not in (Done, Closed) AND assignee="+conf.jiraUser())
    val result = searchPromise.claim()
    result.getIssues.foreach(println)


    val paymoClient = Client.create()
    paymoClient.addFilter(new HTTPBasicAuthFilter(conf.paymoUser(), conf.paymoPassword()))
    val paymoResult = paymoClient.resource("https://app.paymoapp.com/api/tasks?where=project_id%3D" + conf.paymoProjectId()).accept(MediaType.APPLICATION_XML).get(new GenericType[java.util.List[Tasks]]() {})
    paymoResult(0).getTaskList.foreach(println)
  }
  catch
    {
      case e: Exception => logger.severe("Error: " + e.getMessage)
    }
  logger.info("Done.")
  System.exit(0)
}
