package com.wixpress.premium.domains.service

trait Rule {
  def arity(): Int

  def run(facts: List[Fact]): Option[Fact]
}
