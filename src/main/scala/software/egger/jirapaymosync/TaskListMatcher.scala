package software.egger.jirapaymosync

import com.atlassian.jira.rest.client.api.domain.Issue
import software.egger.jirapaymosync.paymo.Task

class TaskListMatcher(jiraIssues: Iterable[Issue])
{
  def matchWith(paymoTasks: Iterable[Task]): Iterable[TaskMatch] =
  {
    val matching = for (issue <- jiraIssues)
    yield new TaskMatch(Some(issue), paymoTasks find (_.getName startsWith issue.getKey + " "))

    val matchingTasks = matching.filter(_.paymoTask.isDefined).map(_.paymoTask.get)
    val nonMatchingTasks = paymoTasks filterNot (matchingTasks.toList contains _)

    matching ++ (nonMatchingTasks map (task => new TaskMatch(None, Some(task))))
  }
}

