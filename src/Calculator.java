/**
 * Для работы программы необходимо скачать и подгрузить модуль JavaFX.
 * File - Project Structure - Libraries - From Maven, в строке поиска вводим fx,
 * далее из списка выбираем org.openjfx:javafx-fxml:11.0.2
 * Устанавливаем в папку с программой.
 * В Run - Edit Configurations - VM options прописываем
 * --module-path "/Users/pras/IdeaProjects/Calculator/lib" --add-modules javafx.controls,javafx.fxml
 */


import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Calculator extends Application {

    private double result;
    private String logic;
    private TextField view;
    private String expression = "";
    static Font font = Font.font("Arial", 20);
    private int numberOfUnclosedBrackets = 0;
    private boolean isLastEventAnEqualSign = false;
    private boolean isLastEventNumber = false;


    @Override
    public void start(Stage primaryStage) {
        try {
            // Создаём макет объекта
            AnchorPane root = new AnchorPane();
            // Создаём объект сцены
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            // Устанавливаем ширину и высоту окна
            primaryStage.setWidth(450);
            primaryStage.setHeight(405);
            // Устанавливаем, что нельзя изменить размер окна
            primaryStage.setResizable(false);
            // Вызов метода для добавления компонента
            addComp(root);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Метод добавления компонентов
    private void addComp(AnchorPane root) {
        // Добавляем поле ввода
        view = new TextField("0");
        view.setPrefWidth(430);
        view.setPrefHeight(50);
        view.setLayoutX(10);
        view.setLayoutY(15);
        // Установка не/возможности редактирования текста в поле ввода
        view.setEditable(false);
        // Контент поля ввода выравниваем по правому краю
        view.setAlignment(Pos.CENTER_RIGHT);
        view.setFont(font);
        root.getChildren().add(view);

        // Добавляем макет сетки компонентов (кнопок)
        GridPane gridPane = new GridPane();
        // Устанавливаем горизонтальный интервал между компонентами в таблице
        gridPane.setHgap(20);
        // Устанавливаем вертикальный интервал между компонентами в таблице
        gridPane.setVgap(10);
        // Отступ макета от верхнего края
        gridPane.setLayoutY(65);
        gridPane.setPadding(new Insets(10));
        root.getChildren().add(gridPane);

        // Добавляем кнопку равенства

        Button btn_eq = new MyButton("=");
        btn_eq.setOnMouseClicked((EventHandler<Event>) event -> {
            StringBuilder rightBracketsAtTheEnd = new StringBuilder();
            rightBracketsAtTheEnd.append(")".repeat(numberOfUnclosedBrackets));
            expression += rightBracketsAtTheEnd;
            view.setText(expression);
            // Отправляем выражение на обработку
            Solution solution = new Solution();

            result = solution.execute(expression);

            // Отображаем результат в поле отображения
            if (result != Double.POSITIVE_INFINITY && result != Double.NEGATIVE_INFINITY) {
                if (result % 1 == 0) {
                    view.setText(String.valueOf((int) result));
                    expression = String.valueOf((int) result);
                } else {
                    view.setText(String.valueOf(result));
                    expression = String.valueOf(result);
                }
            }
            if (result == Double.POSITIVE_INFINITY) {
                view.setText(String.valueOf(Double.POSITIVE_INFINITY));
            }
            if (result == Double.NEGATIVE_INFINITY) {
                view.setText(String.valueOf(Double.NEGATIVE_INFINITY));
            }

            numberOfUnclosedBrackets = 0;
            isLastEventNumber = false;
            isLastEventAnEqualSign = true;
        });
        gridPane.add(btn_eq, 3, 4);


        Button btn_del = new MyButton("del");
        btn_del.setOnMouseClicked((EventHandler<Event>) event -> delete());
        gridPane.add(btn_del, 3, 0);


        Button btn_C = new MyButton("C");
        btn_C.setOnMouseClicked((EventHandler<Event>) event -> clean());
        gridPane.add(btn_C, 4, 0);


        // Создаём объект цифрового слушателя
        NumberEvent numberEvent = new NumberEvent();

        // Добавляем кнопки
        Button btn_1 = new MyButton("1");
        btn_1.setOnMouseClicked(numberEvent);
        gridPane.add(btn_1, 1, 3);

        Button btn_2 = new MyButton("2");
        btn_2.setOnMouseClicked(numberEvent);
        gridPane.add(btn_2, 2, 3);

        Button btn_3 = new MyButton("3");
        btn_3.setOnMouseClicked(numberEvent);
        gridPane.add(btn_3, 3, 3);

        Button btn_4 = new MyButton("4");
        btn_4.setOnMouseClicked(numberEvent);
        gridPane.add(btn_4, 1, 2);

        Button btn_5 = new MyButton("5");
        btn_5.setOnMouseClicked(numberEvent);
        gridPane.add(btn_5, 2, 2);

        Button btn_6 = new MyButton("6");
        btn_6.setOnMouseClicked(numberEvent);
        gridPane.add(btn_6, 3, 2);

        Button btn_7 = new MyButton("7");
        btn_7.setOnMouseClicked(numberEvent);
        gridPane.add(btn_7, 1, 1);

        Button btn_8 = new MyButton("8");
        btn_8.setOnMouseClicked(numberEvent);
        gridPane.add(btn_8, 2, 1);

        Button btn_9 = new MyButton("9");
        btn_9.setOnMouseClicked(numberEvent);
        gridPane.add(btn_9, 3, 1);

        Button btn_0 = new MyButton("0");
        btn_0.setOnMouseClicked(numberEvent);
        gridPane.add(btn_0, 1, 4);


        // Создаём объект слушателя математических операций
        LogicEvent logicEvent = new LogicEvent();

        // Добавляем кнопки
        Button btn_add = new MyButton("+");
        btn_add.setOnMouseClicked(logicEvent);
        gridPane.add(btn_add, 4, 4);

        Button btn_sub = new MyButton("-");
        btn_sub.setOnMouseClicked(logicEvent);
        gridPane.add(btn_sub, 4, 3);

        Button btn_mul = new MyButton("*");
        btn_mul.setOnMouseClicked(logicEvent);
        gridPane.add(btn_mul, 4, 2);

        Button btn_div = new MyButton("/");
        btn_div.setOnMouseClicked(logicEvent);
        gridPane.add(btn_div, 4, 1);

        Button btn_left_bracket = new MyButton("(");
        btn_left_bracket.setOnMouseClicked(logicEvent);
        gridPane.add(btn_left_bracket, 1, 0);

        Button btn_right_bracket = new MyButton(")");
        btn_right_bracket.setOnMouseClicked(logicEvent);
        gridPane.add(btn_right_bracket, 2, 0);

        Button btn_dot = new MyButton(".");
        btn_dot.setOnMouseClicked(logicEvent);
        gridPane.add(btn_dot, 2, 4);

        Button btn_pow = new MyButton("pow");
        btn_pow.setOnMouseClicked(logicEvent);
        gridPane.add(btn_pow, 0, 3);

        Button btn_sqrt = new MyButton("sqrt");
        btn_sqrt.setOnMouseClicked(logicEvent);
        gridPane.add(btn_sqrt, 0, 4);

        Button btn_sin = new MyButton("sin");
        btn_sin.setOnMouseClicked(logicEvent);
        gridPane.add(btn_sin, 0, 0);

        Button btn_cos = new MyButton("cos");
        btn_cos.setOnMouseClicked(logicEvent);
        gridPane.add(btn_cos, 0, 1);

        Button btn_tan = new MyButton("tan");
        btn_tan.setOnMouseClicked(logicEvent);
        gridPane.add(btn_tan, 0, 2);
    }


    public static void main(String[] args) {
        launch(args);
    }


    class NumberEvent implements EventHandler<Event> {
        public void handle(Event event) {
            if (isLastEventAnEqualSign) {
                clean();
            }
            // Получаем источник события (получаем кнопку, которая вызвала событие)
            MyButton btn = (MyButton) event.getSource();
            // Сначала получаем содержимое текущей кнопки
            String btnNumber = btn.getText();
            expression += btnNumber;
            view.setText(expression);
            isLastEventNumber = true;
            isLastEventAnEqualSign = false;
        }
    }


    class LogicEvent implements EventHandler<Event> {
        public void handle(Event event) {
            if (isLastEventAnEqualSign) {
                clean();
            }
            // Получаем кнопку
            MyButton logicBtn = (MyButton) event.getSource();
            // Получаем содержимое кнопки
            logic = logicBtn.getText();

            switch (logic) {
                case "sin" -> {
                    if (isLastEventNumber) {
                        expression += "*";
                    }
                    logic = "sin(";
                    numberOfUnclosedBrackets++;
                }
                case "cos" -> {
                    if (isLastEventNumber) {
                        expression += "*";
                    }
                    logic = "cos(";
                    numberOfUnclosedBrackets++;
                }
                case "tan" -> {
                    if (isLastEventNumber) {
                        expression += "*";
                    }
                    logic = "tan(";
                    numberOfUnclosedBrackets++;
                }
                case "sqrt" -> {
                    if (isLastEventNumber) {
                        expression += "*";
                    }
                    logic = "sqrt(";
                    numberOfUnclosedBrackets++;
                }
                case "(" -> numberOfUnclosedBrackets++;
                case ")" -> numberOfUnclosedBrackets--;
                case "pow" -> {
                    if (!isLastEventNumber){
                        return;
                    } else {
                        logic = "^";
                    }
                }
                case "/", "*", "-", "+" -> {
                    if (!isLastEventNumber){
                        return;
                    }
                }
            }

            isLastEventNumber = false;
            isLastEventAnEqualSign = false;

            expression += logic;
            view.setText(expression);
        }
    }


    // Очищаем поле ввода и переменные, участвующие в логике калькулятора
    private void clean() {
        result = 0.0;
        numberOfUnclosedBrackets = 0;
        isLastEventAnEqualSign = false;
        isLastEventNumber = false;
        logic = null;
        view.setText("0");
        expression = "";
    }


    // Удаляем крайний символ в выражении в каждый конкретный момент
    private void delete() {
        if (expression.endsWith(")")) {
            numberOfUnclosedBrackets++;
        }
        if (expression.endsWith("(")) {
            numberOfUnclosedBrackets--;
        }
        expression = expression.substring(0, expression.length() - 1);
        view.setText(expression);
    }
}