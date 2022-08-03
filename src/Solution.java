import java.util.ArrayList;
import java.util.List;

public class Solution {

    public double execute(final String expression) {
        List<Lexeme> lexemes = new ArrayList<>();

        lexAnalyze(lexemes, expression);

        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);

        double result = expr(lexemeBuffer);
        // Проверяем на деление на ноль
        if (result != Double.POSITIVE_INFINITY && result != Double.NEGATIVE_INFINITY) {
            // округляем до 15 знаков после запятой
            result = Math.round(result * 1_000_000_000_000_000L) / 1_000_000_000_000_000.0;
        }
        return result;
    }


    public static void lexAnalyze(List<Lexeme> lexemes, String expText) {
        char[] expr = expText.toCharArray();
        int pos = 0;
        while (pos < expr.length) {
            char c = expr[pos];
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                    pos++;
                    break;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                    pos++;
                    break;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                    pos++;
                    break;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                    pos++;
                    break;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                    pos++;
                    break;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                    pos++;
                    break;
                case '^':
                    lexemes.add(new Lexeme(LexemeType.POW, c));
                    pos++;
                    break;
                case '.':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    // если первый символ десятичный разделитель и это последний символ выражения или следующий символ не цифра
                    if (expr[pos] == '.' && (pos + 1 >= expr.length || expr[pos + 1] < '0' || expr[pos + 1] > '9')) {
                        throw new RuntimeException("Unexpected character: " + expr[pos] + " at position " + pos);
                    }
                    double value = 0.0;
                    // накопление целой части
                    for (; pos < expr.length && expr[pos] >= '0' && expr[pos] <= '9'; pos++) {
                        value = 10 * value + (expr[pos] - '0');
                    }
                    if (pos < expr.length && expr[pos] == '.') {
                        pos++;
                        // множитель для десятичных разрядов
                        double factor = 1.0;
                        for (; pos < expr.length && expr[pos] >= '0' && expr[pos] <= '9'; pos++) {
                            // уменьшение множителя в 10 раз
                            factor *= 0.1;
                            // добавление десятичной позиции
                            value += (expr[pos] - '0') * factor;
                        }
                    }
                    lexemes.add(new Lexeme(LexemeType.NUMBER, value));
                    break;
                default:
                    if (pos + 3 < expr.length && expr[pos] == 'c' && expr[pos + 1] == 'o' && expr[pos + 2] == 's' && expr[pos + 3] == '(') {
                        lexemes.add(new Lexeme(LexemeType.COS, "cos("));
                        pos += 4;
                    } else {
                        if (pos + 3 < expr.length && expr[pos] == 's' && expr[pos + 1] == 'i' && expr[pos + 2] == 'n' && expr[pos + 3] == '(') {
                            lexemes.add(new Lexeme(LexemeType.SIN, "sin("));
                            pos += 4;
                        } else {
                            if (pos + 3 < expr.length && expr[pos] == 't' && expr[pos + 1] == 'a' && expr[pos + 2] == 'n' && expr[pos + 3] == '(') {
                                lexemes.add(new Lexeme(LexemeType.TAN, "tan("));
                                pos += 4;
                            } else {
                                if (pos + 4 < expr.length && expr[pos] == 's' && expr[pos + 1] == 'q' && expr[pos + 2] == 'r' && expr[pos + 3] == 't' && expr[pos + 4] == '(') {
                                    lexemes.add(new Lexeme(LexemeType.SQRT, "sqrt("));
                                    pos += 5;
                                } else {
                                    if (c != ' ') {
                                        throw new RuntimeException("Unexpected character: " + expr[pos] + " at position " + pos);
                                    }
                                    pos++;
                                }

                            }
                        }
                    }
                    break;
            }
        }
        // если выражение пустое, оно равно нулю
        if (lexemes.size() == 0) {
            lexemes.add(new Lexeme(LexemeType.NUMBER, 0.0));
        }
        lexemes.add(new Lexeme(LexemeType.EOF, ""));
    }


//    Грамматика
//    expr : plus_minus EOF ;
//    plus_minus : multiply_divide ( ( '+' | '-' ) multiply_divide )* ;
//    multiply_divide : pow ( ( '*' | '/' ) pow )* ;
//    pow : factor ( '^' factor )*
//    factor : NUMBER | '-' pow | ( '(' | 'cos(' | 'sin(' | 'tan(' ) plus_minus ')'

    public static double expr(LexemeBuffer lexemes) {
        double value = plus_minus(lexemes);
        Lexeme lexeme = lexemes.next();
        if (lexeme.type != LexemeType.EOF) {
            throw new RuntimeException("Unexpected token: " + lexeme.title + " at position " + lexemes.getPos());
        }
        return value;
    }

    public static double plus_minus(LexemeBuffer lexemes) {
        double value = multiply_divide(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_PLUS -> value += multiply_divide(lexemes);
                case OP_MINUS -> value -= multiply_divide(lexemes);
                case EOF, RIGHT_BRACKET -> {
                    lexemes.back();
                    return value;
                }
                default -> throw new RuntimeException("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos());
            }
        }
    }

    public static double multiply_divide(LexemeBuffer lexemes) {
        double value = pow(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_MUL -> value *= pow(lexemes);
                case OP_DIV -> value /= pow(lexemes);
                case EOF, RIGHT_BRACKET, OP_PLUS, OP_MINUS -> {
                    lexemes.back();
                    return value;
                }
                default -> throw new RuntimeException("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos());
            }
        }
    }

    public static double pow(LexemeBuffer lexemes) {
        double value = factor(lexemes);
        while (true) {
            Lexeme token = lexemes.next();
            if (token.type == LexemeType.POW) {
                value = Math.pow(value, factor(lexemes));
            } else {
                lexemes.back();
                return value;
            }
        }
    }

    public static double factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case NUMBER:
                return lexeme.value;
            case OP_MINUS:
                return -pow(lexemes);
            case SQRT:
            case LEFT_BRACKET:
            case COS:
            case SIN:
            case TAN:
                double value;
                if (lexeme.type == LexemeType.LEFT_BRACKET) {
                    value = plus_minus(lexemes);
                } else {
                    if (lexeme.type == LexemeType.COS) {
                        value = Math.cos(Math.toRadians(plus_minus(lexemes)));
                    } else {
                        if (lexeme.type == LexemeType.SIN) {
                            value = Math.sin(Math.toRadians(plus_minus(lexemes)));
                        } else {
                            if (lexeme.type == LexemeType.TAN) {
                                value = Math.tan(Math.toRadians(plus_minus(lexemes)));
                            } else {
                                value = Math.sqrt(plus_minus(lexemes));
                            }
                        }
                    }
                }
                lexeme = lexemes.next();
                if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw new RuntimeException("Unexpected token: " + lexeme.title + " at position " + lexemes.getPos());
                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + lexeme.title + " at position " + lexemes.getPos());
        }
    }

}