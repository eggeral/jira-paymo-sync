package software.egger.jirapaymosync

import com.atlassian.jira.rest.client.api.domain.Issue
import software.egger.jirapaymosync.paymo.Task
import scala.language.implicitConversions

object TaskIssueImplicits
{
  implicit def issueToTaskListMatcher(jiraIssues: Iterable[Issue]): TaskListMatcher =
    new TaskListMatcher(jiraIssues)

  implicit def issueToTask(issue: Issue): Task = new Task(s"${issue.getKey} ${issue.getSummary}")

}
