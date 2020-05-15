package de.tbollmeier.grammarous

open class Grammar {

    private var rules = mutableMapOf<String, Parser>()
    private var transformations = mutableMapOf<String, AstTransformFn>()
    var startRuleName : String? = null

    fun defineRule(name: String, vararg parsers: Parser) {
        if (rules.isEmpty()) {
            startRuleName = name
        }
        rules[name] = asSingle(parsers)
    }

    fun terminal(tokenType: String, id: String="") : Parser {
        return TokenParser(tokenType, this, id)
    }

    fun rule(name: String, id: String = "") : Parser {
        return RuleParser(name, this, id)
    }

    fun sequence(vararg parsers: Parser) = SequenceParser(*parsers)

    fun oneOf(vararg parsers: Parser) = OneOfParser(*parsers)

    fun optional(vararg parsers: Parser) = MinMaxParser(asSingle(parsers), 0, 1)

    fun many(vararg parsers: Parser) = MinMaxParser(asSingle(parsers), 0)

    fun oneOrMore(vararg parsers: Parser) = MinMaxParser(asSingle(parsers), 1)

    fun transform(name: String, transformFn: AstTransformFn) {
        transformations[name] = transformFn
    }

    fun getTransform(name: String) = transformations[name]

    fun getRule(name: String) = rules[name] ?: throw throw Exception("Undefined production rule $name")

    private fun asSingle(parsers: Array<out Parser>) : Parser {
        return if (parsers.size == 1)
            parsers[0]
        else
            SequenceParser(*parsers)
    }

}