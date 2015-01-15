package software.egger.jirapaymosync

import java.util
import java.util.logging.Logger
import javax.ws.rs.core.{MediaType, UriBuilder}

import com.sun.jersey.api.client.{Client, GenericType}
import software.egger.jirapaymosync.paymo.{Task, Tasks}

import scala.collection.JavaConversions._

class PaymoClient(private val restClient: Client)
{
  private val paymoApiUri = "https://app.paymoapp.com/api/"

  private val logger = Logger.getLogger(classOf[PaymoClient].toString)

  def getPaymoTaskList(projectId: String): List[Task] =
  {
    logger.info(s"Get tasks for project: $projectId")
    val uri = UriBuilder.fromUri(paymoApiUri).path("tasks").queryParam("where", s"project_id=$projectId").build()
    val paymoResult = restClient.resource(uri).accept(MediaType.APPLICATION_XML).get(new GenericType[util.List[Tasks]]()
    {})
    paymoResult(0).getTaskList.toList
  }

  def createTask(taskListId: String, task: Task): Unit =
  {
    logger.info(s"Creating task ${task.getName} in task list: $taskListId")

    val uri = UriBuilder.fromUri(paymoApiUri).path("tasks").queryParam("name", task.getName).queryParam("tasklist_id", taskListId).build()
    restClient.resource(uri).accept(MediaType.APPLICATION_XML).post(new GenericType[util.List[Tasks]]()
    {})
  }
}
