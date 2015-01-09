package software.egger.jirapaymosync

import java.net.URI
import java.util.logging.Logger

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import org.rogach.scallop.ScallopConf
import scala.collection.JavaConversions._

class Conf(arguments: Seq[String]) extends ScallopConf(arguments)
{
  val trustStore = opt[String]()
  val uri = opt[String](required = true)
  val username = opt[String](required = true)
  val password = opt[String](required = true)
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
    val uri = new URI(conf.uri())

    val client = factory.createWithBasicHttpAuthentication(uri, conf.username(), conf.password())

    val searchPromise = client.getSearchClient.searchJql("status not in (Done, Closed) AND assignee="+conf.username())
    val result = searchPromise.claim()
    result.getIssues.foreach(println)
  }
  catch
    {
      case e: Exception => logger.severe("Error: " + e.getMessage)
    }
  logger.info("Done.")
  System.exit(0)
}
