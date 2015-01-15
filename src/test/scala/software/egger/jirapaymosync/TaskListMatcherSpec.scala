package software.egger.jirapaymosync

import java.net.URI

import com.atlassian.jira.rest.client.api.domain._
import org.joda.time.DateTime
import software.egger.jirapaymosync.paymo.Task
import software.egger.jirapaymosync.TaskIssueImplicits._


class TaskListMatcherSpec extends Spec
{

  "A TaskMatcher" should "match existing Jira issues and Paymo tasks" in
    {
      //given
      val jiraIssues = issue("key1", "sum1") :: issue("key2", "sum2") :: Nil
      val paymoTasks = new Task(1234, "key1 sum1") :: new Task(2345, "key2 sum2") :: Nil

      //when
      val taskMatches = jiraIssues matchWith paymoTasks

      //then
      taskMatches.size should be(2)

      taskMatches.head.jiraIssue.get.getKey should be("key1")
      taskMatches.head.paymoTask.get.getId should be(1234)

      taskMatches.take(1).head.jiraIssue.get.getKey should be("key2")
      taskMatches.take(1).head.paymoTask.get.getId should be(2345)
    }

  "A TaskMatcher" should "match Jira issues and Paymo tasks to None if no match can be found" in
    {
      //given
      val jiraIssues = issue("key1", "sum1")  :: Nil
      val paymoTasks = new Task(2345, "key2 sum2") :: Nil

      //when
      val taskMatches = jiraIssues matchWith paymoTasks

      //then
      taskMatches.size should be(2)

      taskMatches.head.jiraIssue.get.getKey should be("key1")
      taskMatches.head.paymoTask should be(None)

      taskMatches.take(1).head.jiraIssue should be(None)
      taskMatches.take(1).head.paymoTask.get.getId should be(2345)
    }


  private def issue(key: String, summary: String): Issue =
  {
    new Issue(summary,
      URI.create("dummy://uri"),
      key,
      1234l,
      new BasicProject(URI.create("dummy://uri"), "dummy", null, null),
      null,
      null,
      "description",
      null,
      null,
      null,
      null,
      null,
      new DateTime(),
      new DateTime(),
      new DateTime(),
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    )


  }

}

