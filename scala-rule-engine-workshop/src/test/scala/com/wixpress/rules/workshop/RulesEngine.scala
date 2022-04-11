package com.wixpress.premium.domains.service

/**
  * Based on blog post: two-minutes-scala-rules-engine
  * http://blog.dossot.net/2008/10/two-minutes-scala-rules-engine.html
  */
class RulesEngine {

  def processUntilDone(factBase : Set[Fact], ruleBase : List[Rule]) : Set[Fact] = {
    // call process until the fact base stabilizes
    processRules(factBase, ruleBase) match {
      case processedFactBase if (processedFactBase == factBase)
      => factBase

      case processedFactBase
      => processUntilDone(processedFactBase, ruleBase)
    }
  }

  def processRules(factBase : Set[Fact], ruleBase : List[Rule]) : Set[Fact] =
    ruleBase match {
      case rule :: tail
      => processOneRule(factBase, rule) ++ processRules(factBase, tail)

      case Nil
      => factBase
    }

  def processOneRule(factBase : Set[Fact], rule : Rule): Set[Fact] =
    Set() ++ factBase.toList.combinations(rule.arity()) // computes all the factorial permutations of the source list of items according to the requested arity
      .flatMap(facts => rule.run(facts))

}
