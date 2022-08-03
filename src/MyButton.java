import javafx.scene.control.Button;

public class MyButton extends Button {

    public MyButton(String text) {
        super(text);
        setFont(Calculator.font);
        setMinSize(70, 50);
    }

}
