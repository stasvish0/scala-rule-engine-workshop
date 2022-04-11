package com.wixpress.premium.domains.service

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class RulesEngineWorkshopTest extends SpecificationWithJUnit {

  "RulesEngineWorkshopTest" should {

    "1. [New Rule] should mark Peter as premium customer" in new Ctx {
      rulesEngine.processUntilDone(factBase, ruleBase) must
        contain(Fact("Premium", "Peter Miller"))
    }

    "2. [New Rule] should grand Peter a discount" in new Ctx {
      rulesEngine.processUntilDone(factBase, ruleBase) must
        contain(Fact("Discount", "Peter Miller", "Porsche", "7.5%"))
    }

    "3. [New Fact] should know that 'VW Beetle' as 'Junk' car" in new Ctx {
      rulesEngine.processUntilDone(factBase, ruleBase) must
        contain(Fact("Junk", "VW Beetle"))
    }

    "4. [New Fact] should know that 'StasW' as low spending customer" in new Ctx {
      rulesEngine.processUntilDone(factBase, ruleBase) must
        contain(Fact("Spending", "StasW", 2007, 55))
    }

    "5. [New Logic] should label StasW as cheap customer" in new Ctx {
      rulesEngine.processUntilDone(factBase, ruleBase) must
        contain(Fact("Cheap", "StasW"))
    }

    "6. [New Logic] should grant StasW a junk yard discount" in new Ctx {
      rulesEngine.processUntilDone(factBase, ruleBase) must
        contain(Fact("Discount", "StasW", "VW Beetle", "5%"))
    }
  }

  abstract class Ctx extends Scope {
    val ruleBase = DevNullRule :: Nil

    val factBase = Set[Fact] (
      Fact("Luxury", "Porsche"),
      Fact("Regular", "Honda"),
      Fact("Spending", "Peter Miller", 2007, 5500),
      Fact("Spending", "John Doe", 2007, 4500)
    )

    val rulesEngine = new RulesEngine
  }

  object DevNullRule extends Rule {
    override def arity(): Int = 0

    override def run(facts: List[Fact]): Option[Fact] = {
      facts match {
        case _
        => None
      }
    }
  }

}


