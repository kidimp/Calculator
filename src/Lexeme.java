public class Lexeme {
    LexemeType type;
    double value;
    String title;

    public Lexeme(LexemeType type, char title) {
        this.type = type;
        value = Double.NaN;
        this.title = String.valueOf(title);
    }

    public Lexeme(LexemeType type, double value) {
        this.type = type;
        this.value = value;
        title = String.valueOf(value);
    }

    public Lexeme(LexemeType type, String title) {
        this.type = type;
        value = Double.NaN;
        this.title = title;
    }
}
