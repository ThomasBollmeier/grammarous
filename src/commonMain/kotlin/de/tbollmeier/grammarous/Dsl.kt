package de.tbollmeier.grammarous


fun grammar(block: GrammarBuilder.() -> Unit): Grammar {
    val g = Grammar()
    GrammarBuilder(g).block()
    return g
}


class GrammarBuilder(private val grammar: Grammar) {

    fun ruleDef(name: String, block: RuleBuilder.() -> Unit) {
        val ruleBuilder = RuleBuilder(grammar)
        ruleBuilder.block()
        grammar.defineRule(name, *ruleBuilder.getParsers().toTypedArray())
    }

    fun transform(name: String, transformFn: AstTransformFn) {
        grammar.transform(name, transformFn)
    }

}

class RuleBuilder(private val grammar: Grammar) {

    private val parsers = mutableListOf<Parser>()

    fun rule(name: String, id: String="") {
        parsers.add(grammar.rule(name, id))
    }

    fun terminal(tokenType: String, id: String="") {
        parsers.add(grammar.terminal(tokenType, id))
    }

    fun sequence(block: RuleBuilder.() -> Unit) = runMethod({ grammar.sequence(*it) }, block)

    fun oneOf(block: RuleBuilder.() -> Unit) = runMethod({ grammar.oneOf(*it) }, block)

    fun optional(block: RuleBuilder.() -> Unit) = runMethod({ grammar.optional(*it) }, block)

    fun many(block: RuleBuilder.() -> Unit) = runMethod({ grammar.many(*it) }, block)

    fun oneOrMore(block: RuleBuilder.() -> Unit) = runMethod({ grammar.oneOrMore(*it) }, block)

    private fun runMethod(parserFn: (Array<out Parser>) -> Parser, block: RuleBuilder.() -> Unit) {
        val ruleBuilder = RuleBuilder(grammar)
        ruleBuilder.block()
        val parser = parserFn(ruleBuilder.getParsers().toTypedArray())
        parsers.add(parser)
    }

    fun getParsers(): List<Parser> = parsers
}