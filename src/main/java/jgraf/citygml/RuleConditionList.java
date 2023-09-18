package jgraf.citygml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RuleConditionList {

    private enum RuleComparisonOperator {
        EQUALS("="),
        NOT_EQUALS("!="),
        LESS_THAN("<"),
        LESS_THAN_OR_EQUALS("<="),
        GREATER_THAN(">"),
        GREATER_THAN_OR_EQUALS(">=");
        private final String symbol;

        RuleComparisonOperator(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        static RuleComparisonOperator fromSymbol(String symbol) {
            for (RuleComparisonOperator op : RuleComparisonOperator.values()) {
                if (op.getSymbol().equals(symbol)) {
                    return op;
                }
            }
            throw new IllegalArgumentException("No comparison operator with symbol " + symbol);
        }
    }

    private enum RuleVariableMarkup {
        PREFIX("$");
        private final String symbol;

        RuleVariableMarkup(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        static RuleVariableMarkup fromSymbol(String symbol) {
            for (RuleVariableMarkup vm : RuleVariableMarkup.values()) {
                if (vm.getSymbol().equals(symbol)) {
                    return vm;
                }
            }
            throw new IllegalArgumentException("No variable markup with symbol " + symbol);
        }
    }

    public enum RuleWeightWildCard {
        DYNAMIC("*");
        private final String symbol;

        RuleWeightWildCard(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        static RuleWeightWildCard fromSymbol(String symbol) {
            for (RuleWeightWildCard wc : RuleWeightWildCard.values()) {
                if (wc.getSymbol().equals(symbol)) {
                    return wc;
                }
            }
            throw new IllegalArgumentException("No weight wildcard with symbol " + symbol);
        }
    }

    public enum RuleValuesWildCard {
        FOR_ALL("*");
        private final String symbol;

        RuleValuesWildCard(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        static RuleValuesWildCard fromSymbol(String symbol) {
            for (RuleValuesWildCard wc : RuleValuesWildCard.values()) {
                if (wc.getSymbol().equals(symbol)) {
                    return wc;
                }
            }
            throw new IllegalArgumentException("No values wildcard with symbol " + symbol);
        }
    }

    // A rule condition tuple is a key-value pair with an operator
    // Such as "x=0", "y<1", "z=*", "t=$value"
    public static class RuleConditionTuple {
        private final String key;
        private final RuleComparisonOperator op;
        private String value;
        private final boolean hasWildcard;
        private final boolean hasVariable;
        private final double precision;

        public RuleConditionTuple(String condition, double precision) {
            // Check if condition matches the pattern "prop op value"
            String opRegEx = String.join("|", Arrays.stream(RuleComparisonOperator.values())
                    .map(RuleComparisonOperator::getSymbol).toList());
            if (!condition.matches("^([a-zA-Z_][a-zA-Z0-9_]+)\\s*(" + opRegEx + ")\\s*.*$")) {
                // "^([a-zA-Z_-]+)\\s*(=|<|>|<=|>=|!=)\\s*([[+-]?\\d\\w*.]+)$"
                throw new IllegalArgumentException("Invalid condition: " + condition);
            }

            String[] parts = condition.split(opRegEx);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid condition: " + condition);
            }

            this.key = parts[0].replaceAll("\\s+", "");
            this.value = parts[1].replaceAll("\\s+", "");

            RuleComparisonOperator op = RuleComparisonOperator.fromSymbol(condition
                    .replace(parts[0], "").replace(parts[1], ""));
            if (this.value.matches("true|false")
                    && (op != RuleComparisonOperator.EQUALS && op != RuleComparisonOperator.NOT_EQUALS)) {
                logger.warn("Boolean value {} used with operator {}, changing to {}",
                        this.value, op.getSymbol(), RuleComparisonOperator.NOT_EQUALS.symbol);
                this.op = RuleComparisonOperator.NOT_EQUALS;
            } else {
                this.op = op;
            }

            this.hasWildcard = value.equals(RuleValuesWildCard.FOR_ALL.symbol);
            this.hasVariable = value.startsWith(RuleVariableMarkup.PREFIX.symbol);
            this.precision = precision;
        }

        public String getKey() {
            return key;
        }

        public RuleComparisonOperator getOp() {
            return op;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean hasVariable() {
            return hasVariable;
        }

        public double getPrecision() {
            return precision;
        }

        public boolean eval(Map<String, Object> props) {
            if (key.startsWith(RuleVariableMarkup.PREFIX.symbol)) {
                return true; // Variables will be checked later
            }
            Object value = props.get(key);
            if (value == null) {
                logger.warn("Property {} not found in properties", key);
                return false;
            }
            return eval(value.toString());
        }

        private boolean eval(String otherValue) {
            otherValue = otherValue.replaceAll("\\s*", "");
            String regex = "[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)";
            boolean valueMatched;
            boolean isValueNumeric = value.matches(regex);
            boolean isOtherValueNumeric = otherValue.matches(regex);
            if (isValueNumeric && isOtherValueNumeric) {
                double delta = Double.parseDouble(value) - Double.parseDouble(otherValue);
                valueMatched = Math.abs(delta) <= precision;
                return switch ((RuleComparisonOperator) op) {
                    case EQUALS -> valueMatched;
                    case NOT_EQUALS -> !valueMatched;
                    case LESS_THAN -> delta < -precision;
                    case LESS_THAN_OR_EQUALS -> delta <= -precision;
                    case GREATER_THAN -> delta > precision;
                    case GREATER_THAN_OR_EQUALS -> delta >= precision;
                };
            } else {
                boolean isBoolean = value.toLowerCase().matches("true|false");
                boolean isOtherBoolean = otherValue.toLowerCase().matches("true|false");
                if (isBoolean && isOtherBoolean) {
                    valueMatched = Boolean.parseBoolean(value) == Boolean.parseBoolean(otherValue);
                    return switch ((RuleComparisonOperator) op) {
                        case EQUALS -> valueMatched;
                        case NOT_EQUALS -> !valueMatched;
                        default -> false;
                    };
                }
                try {
                    ZonedDateTime date = ZonedDateTime.parse(value);
                    ZonedDateTime otherDate = ZonedDateTime.parse(otherValue);
                    valueMatched = date.isEqual(otherDate);
                    return switch ((RuleComparisonOperator) op) {
                        case EQUALS -> valueMatched;
                        case NOT_EQUALS -> !valueMatched;
                        case LESS_THAN -> date.isBefore(otherDate);
                        case LESS_THAN_OR_EQUALS -> date.isBefore(otherDate) || valueMatched;
                        case GREATER_THAN -> date.isAfter(otherDate);
                        case GREATER_THAN_OR_EQUALS -> date.isAfter(otherDate) || valueMatched;
                    };
                } catch (DateTimeParseException e) {
                    int compare = value.compareTo(otherValue);
                    return switch ((RuleComparisonOperator) op) {
                        case EQUALS -> compare == 0;
                        case NOT_EQUALS -> compare != 0;
                        case LESS_THAN -> compare < 0;
                        case LESS_THAN_OR_EQUALS -> compare <= 0;
                        case GREATER_THAN -> compare > 0;
                        case GREATER_THAN_OR_EQUALS -> compare >= 0;
                    };
                }
            }
        }

        @Override
        public String toString() {
            return key + op.getSymbol() + value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RuleConditionTuple other)) return false;
            String regex = "[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)";
            boolean valueMatched;
            boolean isValueNumeric = value.matches(regex);
            boolean isOtherValueNumeric = other.value.matches(regex);
            if (isValueNumeric && isOtherValueNumeric) {
                valueMatched = Math.abs(Double.parseDouble(value) - Double.parseDouble(other.value)) <= precision;
            } else {
                boolean isBoolean = value.toLowerCase().matches("true|false");
                boolean isOtherBoolean = other.value.toLowerCase().matches("true|false");
                if (isBoolean && isOtherBoolean) {
                    valueMatched = Boolean.parseBoolean(value) == Boolean.parseBoolean(other.value);
                } else {
                    try {
                        ZonedDateTime date = ZonedDateTime.parse(value);
                        ZonedDateTime otherDate = ZonedDateTime.parse(other.value);
                        valueMatched = date.isEqual(otherDate);
                    } catch (DateTimeParseException e) {
                        valueMatched = value.equals(other.value);
                    }
                }
            }
            return key.equals(other.key) && op.equals(other.op) && valueMatched;
        }
    }

    private final List<RuleConditionTuple> conditions;
    private final double precision;
    private final static Logger logger = LoggerFactory.getLogger(RuleConditionList.class);

    public RuleConditionList(List<RuleConditionTuple> conditions, double precision) {
        this.conditions = conditions;
        this.precision = precision;
    }

    public RuleConditionList(String string, double precision) {
        conditions = new ArrayList<>();
        Arrays.stream(string.split(";"))
                .map(s -> new RuleConditionTuple(s, precision))
                .forEach(t -> {
                    if (conditions.contains(t)) {
                        logger.warn("Duplicate condition {} found, which will be ignored", t);
                    } else {
                        conditions.add(t);
                    }
                });
        this.precision = precision;
    }

    public List<RuleConditionTuple> getConditions() {
        return conditions;
    }

    public double getPrecision() {
        return precision;
    }

    // Conditions check, such as "x=0;y<1;z=*"
    public boolean eval(Map<String, Object> props) {
        if (conditions == null || conditions.isEmpty()) return true;

        if (props == null || props.size() == 0) return false;

        for (RuleConditionTuple condition : conditions) {
            if (!condition.eval(props)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return String.join(";", conditions.stream().map(RuleConditionTuple::toString).toList());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RuleConditionList other)) return false;
        if (conditions.size() != other.conditions.size()) return false;
        return conditions.containsAll(other.conditions);
    }
}
