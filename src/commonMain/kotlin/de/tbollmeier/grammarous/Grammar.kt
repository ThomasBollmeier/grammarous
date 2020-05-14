package de.tbollmeier.grammarous

open class Grammar {

    private var rules = mutableMapOf<String, Parser>()
    private var transformations = mutableMapOf<String, AstTransformFn>()
    var startRuleName : String? = null

    fun defineRule(name: String, parser: Parser) {
        if (rules.isEmpty()) {
            startRuleName = name
        }
        rules[name] = parser
    }

    fun terminal(tokenType: String, id: String="") : Parser {
        return TokenParser(tokenType, this, id)
    }

    fun rule(name: String, id: String = "") : Parser {
        return RuleParser(name, this, id)
    }

    fun sequence(vararg parser: Parser) = SequenceParser(*parser)

    fun oneOf(vararg parser: Parser) = OneOfParser(*parser)

    fun optional(parser: Parser) = MinMaxParser(parser, 0, 1)

    fun many(parser: Parser) = MinMaxParser(parser, 0)

    fun oneOrMore(parser: Parser) = MinMaxParser(parser, 1)

    fun transform(name: String, transformFn: AstTransformFn) {
        transformations[name] = transformFn
    }

    fun getTransform(name: String) = transformations[name]

    fun getRule(name: String) = rules[name] ?: throw throw Exception("Undefined production rule $name")

}