package software.egger.jirapaymosync

import org.scalatest.{FlatSpec, Inside, Inspectors, Matchers, OptionValues}

abstract class Spec extends FlatSpec with Matchers with OptionValues with Inside with Inspectors
