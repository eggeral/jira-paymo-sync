package software.egger.jirapaymosync

import java.util
import javax.ws.rs.core.{UriBuilder, MediaType}

import com.sun.jersey.api.client.{GenericType, Client}
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter
import software.egger.jirapaymosync.paymo.{Tasks, Task}
import scala.collection.JavaConversions._

class PaymoClient(private val user: String, private val password: String)
{
  private val paymoApiUri = "https://app.paymoapp.com/api"
  private val paymoClient = Client.create()
  paymoClient.addFilter(new HTTPBasicAuthFilter(user, password))

  def getPaymoTaskList(projectId: String): List[Task] =
  {
    val uri = UriBuilder.fromUri(paymoApiUri).path("tasks").queryParam("where", s"project_id=$projectId").build()
    val paymoResult = paymoClient.resource(uri).accept(MediaType.APPLICATION_XML).get(new GenericType[util.List[Tasks]](){})
    paymoResult(0).getTaskList.toList
  }

  def createTask(taskListId: String, name: String): Unit = {
    val uri = UriBuilder.fromUri(paymoApiUri).path("tasks").queryParam("name", name).queryParam("tasklist_id", taskListId).build()
    paymoClient.resource(uri).accept(MediaType.APPLICATION_XML).post(new GenericType[util.List[Tasks]]() {})
  }
}
