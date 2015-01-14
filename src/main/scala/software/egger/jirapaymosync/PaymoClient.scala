package software.egger.jirapaymosync

import java.util
import javax.ws.rs.core.{MediaType, UriBuilder}

import com.sun.jersey.api.client.{Client, GenericType}
import software.egger.jirapaymosync.paymo.{Task, Tasks}

import scala.collection.JavaConversions._

class PaymoClient(private val restClient: Client)
{
  val paymoApiUri = "https://app.paymoapp.com/api/"

  def getPaymoTaskList(projectId: String): List[Task] =
  {
    val uri = UriBuilder.fromUri(paymoApiUri).path("tasks").queryParam("where", s"project_id=$projectId").build()
    val paymoResult = restClient.resource(uri).accept(MediaType.APPLICATION_XML).get(new GenericType[util.List[Tasks]]()
    {})
    paymoResult(0).getTaskList.toList
  }

  def createTask(taskListId: String, name: String): Unit =
  {
    val uri = UriBuilder.fromUri(paymoApiUri).path("tasks").queryParam("name", name).queryParam("tasklist_id", taskListId).build()
    restClient.resource(uri).accept(MediaType.APPLICATION_XML).post(new GenericType[util.List[Tasks]]()
    {})
  }
}
