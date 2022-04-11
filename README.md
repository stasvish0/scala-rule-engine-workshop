# Generic Rule Engine Workshop
**how to develop a custom rule engine in scala?**
<br><br>  

- [Getting started](#getting-started)
- [Intro](#Intro)
- [Guided Example](#guided-example)
- [Production use cases](#production-use-cases)
- [Q&A](#q&a)



## Getting started

```
git clone git@github.com:wix-a/scala-rule-engine-workshop.git 
```

- Import Bazel Project

- add `//...` to you bazel project file under `targets`

- Bazel sync the project


## Intro

This workshop is based on a [blog post](http://blog.dossot.net/2008/10/two-minutes-scala-rules-engine.html) I read a few years ago that impressed me in its simplicity.

### What is a rule engine?

A [Rule Engine](https://martinfowler.com/bliki/RulesEngine.html) is all about providing an alternative computational model. 
Instead of the usual imperative model, which consists of code commands in sequence with conditionals and loops, 

A rules engine is based on a Rule System. each of which has a condition and an action. 

Simplistically you can think of it as a bunch of if-then statements :)

A good way of thinking of it is that the system runs through all the rules, 
picks the ones for which the condition is true, and then evaluates the corresponding actions. 

The nice thing about this is that many problems naturally fit this model:

```
  if site.owner.isChannelAcocunt then price = 0;
  if site.buissness.stores >= 1 then premium += 200;
  if site.owner.isWixEmployye && product.price > 0 then price = 0;
```

Another way to look at it is that a rule engine is a big soup of `Facts` and `Rules` which the rules engine keep on steering until the soup is stable and doesn't mutate anymore.

### What is a Fact?

a fact is a named relationship between predicates. 

For example, in "blue is the color of the sky", color is the relationship between blue and sky. The rules engine matches these facts on predefined patterns and infers new facts out of these matches.

```
case class Fact(name: String, predicates: Any*)
```

### What is a Rule?

A rule is a function which given some facts produces more facts.

Each rule tells the engine the number of facts it needs to receive as arguments when called. The engine then feeds the rule with all the unique combinations of facts that match its arity.

```
trait Rule {
  def arity(): Int  

  def run(facts: List[Fact]): List[Fact]
}
```

### How do Rules and Facts interact?

Here comes the rule engine, it's basically a recursive implementation with feeds all the facts through all the rules until eventually the facts base stabilizes.

```
  def processUntilDone(factBase : Set[Fact], ruleBase : List[Rule]) : Set[Fact] = {
    // call process until the fact base stabilizes
    processRules(factBase, ruleBase) match {
      case processedFactBase if (processedFactBase == factBase)
      => factBase

      case processedFactBase
      => processUntilDone(processedFactBase, ruleBase)
    }
  }
```

**Assumption:** A rule execution is bound by CPU only and no IO will happen upon rule execution.
**Assumption:** Initial Facts collection might require IO to fetch the data from multiple resources. 



## Guided Example

With this in place, let us look at the facts and rules I created to solve the [classic RuleML discount problem](http://ruleml.org/papers/tutorial-ruleml-20050513.html) (I added an extra customer).

Here are the initial facts definition:
- Porsche is a Luxury car
- Honda is a Regular car
- Peter Miller spent 5500 during the year 2007
- John Doe spent 4500 during the year 2007

```
val factBase = Set[Fact] (
  Fact("Luxury", "Porsche"),
  Fact("Regular", "Honda"),
  Fact("Spending", "Peter Miller", 2007, 5500),
  Fact("Spending", "John Doe", 2007, 4500)
)
```

### 1. [New Rule] should mark Peter as premium customer

Please go to the test and fix `1. [New Rule] should mark Peter as premium customer` in the file `RulesEngineWorkshopTest`

You will need to define a new rule and to add it to `ruleBase`

Psuedo rule definition:
```
if customer Spending >= 5000 in some year  
     then customer is Premium;
```

### 2. [New Rule] should grand Peter a discount

Please go to the test and fix `2. [New Rule] should grand Peter a discount` in the file `RulesEngineWorkshopTest`

You will need to define a new rule and to add it to `ruleBase`

Psuedo rule definition:
```
if customer is Premium  
     then customer should receive 7.5% discount in buying Luxury cars 
```

### 3. [New Fact] should know that 'VW Beetle' as 'Junk' car

Please go to the test and fix `### 3. [New Fact] should know that 'VW Beetle' as 'Junk' car` in file `RulesEngineWorkshopTest`

You will need to define a new Fact in `factBase`

Psuedo fact definition:
```
VW Beetle is a Junk car 
```

### 4. [New Fact] should know that 'StasW' as low spending customer

Please go to the test and fix `4. [New Fact] should know that 'StasW' as low spending customer` in file `RulesEngineWorkshopTest`

You will need to define a new Fact in `factBase`

Psuedo fact definition:
```
Stasw Spending in 2007 is 55 
```

### 5. [New Logic] should label StasW as cheap customer

Please go to the test and fix `5. [New Logic] should label StasW as cheap customer` in the file `RulesEngineWorkshopTest`

You will need to define a new rule and to add it to `ruleBase`

Psuedo rule definition:
```
if customer Spending < 100 in some year  
     then customer is CCheap; 
```

### 6. [New Logic] should grant StasW a junk yard discount

Please go to the test and fix `6. [New Logic] should grant StasW a junk yard discount` in the file `RulesEngineWorkshopTest`

You will need to define a new rule and to add it to `ruleBase`

Psuedo rule definition:
```
if customer is Cheap  
     then customer should receive 5% discount in buying Junk cars 
```



## Production use cases

### recurring-service
What did we change?
- Added types to Predicates
- Added metadata to facts to carry some more details regrading how fact was collected
- change `Rule.run` interface to allow production of multiple facts instead of just 1
- remove the need to define `Rule.arity` and all rule engine to pass into the rule all facts

Example: `RecurringRulesEngineTest.scala`



## Q&A

