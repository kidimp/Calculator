public enum LexemeType {
    LEFT_BRACKET, RIGHT_BRACKET,    // левая скобка, правая скобка
    OP_PLUS, OP_MINUS,              // сложение, вычитание
    OP_MUL, OP_DIV,                 // умножение, деление
    POW, SQRT,                      // степень, квадратный корень
    COS, SIN, TAN,                  // тригонометрические функции
    NUMBER,                         // число
    EOF                             // конец выражения
}
