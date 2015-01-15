package software.egger.jirapaymosync

import com.atlassian.jira.rest.client.api.domain.Issue
import software.egger.jirapaymosync.paymo.Task

case class TaskMatch(jiraIssue: Option[Issue], paymoTask: Option[Task])

